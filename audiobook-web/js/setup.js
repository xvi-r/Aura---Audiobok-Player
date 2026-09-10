// First-Time Server Setup Wizard Screen Module
import { getApiBase, fetchWithTimeout } from "./config.js";
import { router } from "./router.js";
import { setAuthenticatedUser } from "./auth.js";

export function renderSetupView() {
  const container = document.getElementById("main-content");
  const sidebar = document.getElementById("sidebar");
  const playerBar = document.getElementById("audio-player-bar");

  if (sidebar) sidebar.style.display = "none";
  if (playerBar) playerBar.style.display = "none";

  if (!container) return;

  container.className = "fade-in";
  container.style.overflowY = "auto";
  container.style.padding = "48px 24px";
  container.style.maxWidth = "600px";
  container.style.margin = "0 auto";

  container.innerHTML = `
    <header class="library-header" style="margin-bottom: 24px;">
      <div class="library-header-left">
        <h1 class="library-title">First-Time Setup</h1>
        <p class="library-subtitle">Welcome to Aura! Set up your Administrator account to get started.</p>
      </div>
    </header>

    <div class="auth-view-wrapper">
      <div class="auth-card">
        <div class="auth-card-header">
          <div class="auth-badge-icon">
            <i data-lucide="shield-alert"></i>
          </div>
          <h2 class="auth-card-title">Server Setup Required</h2>
          <p class="auth-card-subtitle">Create the primary Administrator user account for your self-hosted server.</p>
        </div>

        <div id="setup-alert-container"></div>

        <form class="auth-form" id="setup-form" autocomplete="off">
          <div class="auth-field-group">
            <label class="auth-label" for="setup-username-input">
              <i data-lucide="user" style="width: 14px; height: 14px; color: var(--text-muted);"></i>
              Admin Username
            </label>
            <div class="auth-input-wrapper">
              <i data-lucide="user" class="auth-input-icon"></i>
              <input 
                type="text" 
                id="setup-username-input" 
                class="auth-input" 
                placeholder="Enter administrator username" 
                required 
                autocomplete="username"
              />
            </div>
          </div>

          <div class="auth-field-group">
            <label class="auth-label" for="setup-password-input">
              <i data-lucide="lock" style="width: 14px; height: 14px; color: var(--text-muted);"></i>
              Admin Password
            </label>
            <div class="auth-input-wrapper">
              <i data-lucide="lock" class="auth-input-icon"></i>
              <input 
                type="password" 
                id="setup-password-input" 
                class="auth-input" 
                placeholder="Create administrator password" 
                required 
                autocomplete="new-password"
              />
              <button type="button" class="auth-toggle-pwd" id="setup-toggle-pwd" title="Toggle password visibility">
                <i data-lucide="eye" id="setup-pwd-eye-icon"></i>
              </button>
            </div>
          </div>

          <div class="auth-field-group">
            <label class="auth-label" for="setup-confirm-password-input">
              <i data-lucide="shield-check" style="width: 14px; height: 14px; color: var(--text-muted);"></i>
              Confirm Admin Password
            </label>
            <div class="auth-input-wrapper">
              <i data-lucide="shield-check" class="auth-input-icon"></i>
              <input 
                type="password" 
                id="setup-confirm-password-input" 
                class="auth-input" 
                placeholder="Confirm password" 
                required 
                autocomplete="new-password"
              />
            </div>
          </div>

          <button type="submit" class="auth-submit-btn" id="setup-submit-btn">
            <i data-lucide="check-circle-2"></i>
            <span>Complete Server Setup</span>
          </button>
        </form>
      </div>
    </div>
  `;

  if (window.lucide) {
    window.lucide.createIcons();
  }

  // Setup Event Handlers
  const form = document.getElementById("setup-form");
  const togglePwdBtn = document.getElementById("setup-toggle-pwd");
  const pwdInput = document.getElementById("setup-password-input");
  const confirmPwdInput = document.getElementById("setup-confirm-password-input");
  const alertContainer = document.getElementById("setup-alert-container");

  if (togglePwdBtn && pwdInput) {
    togglePwdBtn.addEventListener("click", () => {
      const type = pwdInput.getAttribute("type") === "password" ? "text" : "password";
      pwdInput.setAttribute("type", type);
      if (confirmPwdInput) confirmPwdInput.setAttribute("type", type);
      const eyeIcon = document.getElementById("setup-pwd-eye-icon");
      if (eyeIcon) {
        eyeIcon.setAttribute("data-lucide", type === "password" ? "eye" : "eye-off");
        if (window.lucide) window.lucide.createIcons();
      }
    });
  }

  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      const usernameInput = document.getElementById("setup-username-input");
      const submitBtn = document.getElementById("setup-submit-btn");

      const username = usernameInput ? usernameInput.value.trim() : "";
      const password = pwdInput ? pwdInput.value.trim() : "";
      const confirmPassword = confirmPwdInput ? confirmPwdInput.value.trim() : "";

      if (!username || !password) {
        showSetupAlert(alertContainer, "Please fill in all fields.", "error");
        return;
      }

      if (password !== confirmPassword) {
        showSetupAlert(alertContainer, "Passwords do not match.", "error");
        return;
      }

      submitBtn.disabled = true;
      submitBtn.innerHTML = `<i data-lucide="loader" class="spinner-icon"></i><span>Completing Setup...</span>`;
      if (window.lucide) window.lucide.createIcons();

      const API_BASE = getApiBase();
      const payload = { username, password };

      try {
        console.log("[Aura Setup] Sending POST request to /setup:", payload);
        const response = await fetchWithTimeout(`${API_BASE}/setup`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        }, 8000);

        if (response.ok || response.status === 204) {
          setAuthenticatedUser(username);
          showSetupAlert(alertContainer, "Server setup completed! Logging in as Administrator...", "success");
          setTimeout(() => {
            router.navigate("/library");
          }, 800);
        } else {
          let msg = "Failed to complete setup.";
          try {
            const errData = await response.json();
            if (errData && (errData.message || errData.error)) msg = errData.message || errData.error;
          } catch (e) {}
          showSetupAlert(alertContainer, msg, "error");
        }
      } catch (err) {
        console.error("Setup HTTP Error:", err);
        showSetupAlert(alertContainer, "Could not connect to server to complete setup.", "error");
      } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = `<i data-lucide="check-circle-2"></i><span>Complete Server Setup</span>`;
        if (window.lucide) window.lucide.createIcons();
      }
    });
  }
}

function showSetupAlert(container, message, type = "error") {
  if (!container) return;
  const isError = type === "error";
  container.innerHTML = `
    <div class="auth-alert ${isError ? 'auth-alert-error' : 'auth-alert-success'}">
      <i data-lucide="${isError ? 'alert-circle' : 'check-circle'}" style="width: 16px; height: 16px;"></i>
      <span>${message}</span>
    </div>
  `;
  if (window.lucide) window.lucide.createIcons();
}
