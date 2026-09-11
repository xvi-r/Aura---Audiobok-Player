import { getApiBase, fetchWithTimeout } from "./config.js";

export function renderDiscover(initialAsin = "") {
  const container = document.getElementById("main-content");
  if (!container) return;

  container.innerHTML = `
    <div class="discover-container">
      <div class="discover-header">
        <h1 class="discover-title">
          <i data-lucide="compass"></i>
          Discover Audiobooks
        </h1>
        <p class="discover-subtitle">Search for audiobook metadata by Audible ASIN to view complete details, ratings, and summary.</p>
      </div>

      <div class="discover-search-card">
        <form class="discover-search-form" id="discover-form">
          <div class="discover-input-wrapper">
            <i data-lucide="search"></i>
            <input 
              type="text" 
              id="discover-asin-input" 
              class="discover-input" 
              placeholder="Enter Audible ASIN (e.g. B017V568SY)..." 
              value="${escapeHtml(initialAsin)}" 
              autocomplete="off"
            />
          </div>
          <button type="submit" class="discover-search-btn">
            <i data-lucide="arrow-right"></i>
            <span>Search</span>
          </button>
        </form>
        <div class="discover-suggestions">
          <span>Try example:</span>
          <button class="discover-chip" data-asin="B017V568SY">B017V568SY (Harry Potter 1)</button>
          <button class="discover-chip" data-asin="B002V8OU26">B002V8OU26 (The Hobbit)</button>
          <button class="discover-chip" data-asin="B0797DY74V">B0797DY74V (Atomic Habits)</button>
        </div>
      </div>

      <div id="discover-results-area">
        <div class="discover-state-container">
          <i data-lucide="search" style="width: 42px; height: 42px; stroke-width: 1.5; margin-bottom: 12px; color: var(--text-muted);"></i>
          <p>Enter an ASIN above to retrieve audiobook details.</p>
        </div>
      </div>
    </div>
  `;

  if (window.lucide) {
    window.lucide.createIcons();
  }

  const form = document.getElementById("discover-form");
  const input = document.getElementById("discover-asin-input");
  const suggestions = document.querySelectorAll(".discover-chip");

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const asin = input.value.trim();
    if (asin) {
      performAsinSearch(asin);
    }
  });

  suggestions.forEach((chip) => {
    chip.addEventListener("click", () => {
      const asin = chip.getAttribute("data-asin");
      input.value = asin;
      performAsinSearch(asin);
    });
  });

  if (initialAsin) {
    performAsinSearch(initialAsin);
  }
}

async function performAsinSearch(asin) {
  const resultsArea = document.getElementById("discover-results-area");
  if (!resultsArea) return;

  // Clean ASIN input
  const cleanAsin = asin.trim();

  // Show loading state
  resultsArea.innerHTML = `
    <div class="discover-state-container">
      <div class="discover-spinner"></div>
      <p style="font-weight: 500; color: var(--text-main);">Fetching audiobook details for ASIN ${escapeHtml(cleanAsin)}...</p>
    </div>
  `;

  try {
    const API_BASE = getApiBase();
    const response = await fetchWithTimeout(`${API_BASE}/api/audiobooks/asin/${encodeURIComponent(cleanAsin)}`, {}, 10000);

    if (!response.ok) {
      if (response.status === 404) {
        throw new Error(`No audiobook found for ASIN "${cleanAsin}".`);
      }
      throw new Error(`Server returned HTTP ${response.status} when searching ASIN.`);
    }

    const data = await response.json();
    renderBookResults(data);
  } catch (err) {
    console.error("[Aura Discover] Search error:", err);
    resultsArea.innerHTML = `
      <div class="discover-error-box">
        <i data-lucide="alert-circle" style="width: 32px; height: 32px; margin-bottom: 8px;"></i>
        <h3 style="font-size: 1.1rem; font-weight: 600; margin-bottom: 4px;">Search Failed</h3>
        <p style="font-size: 0.9rem;">${escapeHtml(err.message || "Failed to fetch audiobook metadata.")}</p>
      </div>
    `;
    if (window.lucide) {
      window.lucide.createIcons();
    }
  }
}

function renderBookResults(data) {
  const resultsArea = document.getElementById("discover-results-area");
  if (!resultsArea) return;

  const authorsText = data.authors && data.authors.length > 0 
    ? data.authors.map(a => a.name).join(", ") 
    : "Unknown Author";

  const narratorsText = data.narrators && data.narrators.length > 0 
    ? data.narrators.map(n => n.name).join(", ") 
    : null;

  const coverUrl = data.image || "/assets/covers/art_of_flow.png";

  const ratingVal = data.rating ? parseFloat(data.rating).toFixed(1) : null;

  const runtimeStr = formatRuntime(data.runtimeLengthMin);

  const releaseStr = data.releaseDate 
    ? new Date(data.releaseDate).toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' })
    : null;

  const genresHtml = data.genres && data.genres.length > 0
    ? data.genres.map(g => `<span class="discover-genre-chip ${g.type === 'genre' ? 'genre' : ''}">${escapeHtml(g.name)}</span>`).join("")
    : null;

  const summaryContent = data.summary || data.description || "No description available for this audiobook.";

  resultsArea.innerHTML = `
    <div class="discover-result-card">
      <div class="discover-book-hero">
        <div class="discover-cover-wrapper">
          <img src="${escapeHtml(coverUrl)}" alt="${escapeHtml(data.title || 'Audiobook Cover')}" class="discover-cover-img" onerror="this.src='/assets/covers/art_of_flow.png'" />
        </div>
        <div class="discover-book-meta">
          <h2 class="discover-book-title">${escapeHtml(data.title || "Untitled Audiobook")}</h2>
          <div class="discover-book-author">By ${escapeHtml(authorsText)}</div>
          ${narratorsText ? `<div class="discover-book-narrator">Narrated by ${escapeHtml(narratorsText)}</div>` : ''}

          <div class="discover-stats-row">
            ${ratingVal ? `
              <div class="discover-badge discover-badge-star">
                <i data-lucide="star"></i>
                <span>${ratingVal}</span>
              </div>
            ` : ''}
            
            ${runtimeStr ? `
              <div class="discover-badge">
                <i data-lucide="clock"></i>
                <span>${runtimeStr}</span>
              </div>
            ` : ''}

            ${data.formatType ? `
              <div class="discover-badge">
                <i data-lucide="disc"></i>
                <span>${escapeHtml(data.formatType)}</span>
              </div>
            ` : ''}

            ${data.language ? `
              <div class="discover-badge">
                <i data-lucide="globe"></i>
                <span>${escapeHtml(data.language)}</span>
              </div>
            ` : ''}
          </div>
        </div>
      </div>

      ${genresHtml ? `
        <div class="discover-genres-section">
          <div class="discover-section-title">Categories & Tags</div>
          <div class="discover-genre-tags">
            ${genresHtml}
          </div>
        </div>
      ` : ''}

      <div class="discover-summary-section">
        <div class="discover-section-title">Summary & Details</div>
        <div class="discover-summary-content">
          ${summaryContent}
        </div>
      </div>

      <div class="discover-grid-section">
        <div class="discover-grid-item">
          <div class="discover-grid-label">ASIN</div>
          <div class="discover-grid-value">${escapeHtml(data.asin || "N/A")}</div>
        </div>

        ${data.publisherName ? `
          <div class="discover-grid-item">
            <div class="discover-grid-label">Publisher</div>
            <div class="discover-grid-value">${escapeHtml(data.publisherName)}</div>
          </div>
        ` : ''}

        ${releaseStr ? `
          <div class="discover-grid-item">
            <div class="discover-grid-label">Release Date</div>
            <div class="discover-grid-value">${escapeHtml(releaseStr)}</div>
          </div>
        ` : ''}

        ${data.copyright ? `
          <div class="discover-grid-item">
            <div class="discover-grid-label">Copyright Year</div>
            <div class="discover-grid-value">${escapeHtml(String(data.copyright))}</div>
          </div>
        ` : ''}

        ${data.isbn ? `
          <div class="discover-grid-item">
            <div class="discover-grid-label">ISBN</div>
            <div class="discover-grid-value">${escapeHtml(data.isbn)}</div>
          </div>
        ` : ''}

        ${data.literatureType ? `
          <div class="discover-grid-item">
            <div class="discover-grid-label">Literature Type</div>
            <div class="discover-grid-value">${escapeHtml(data.literatureType)}</div>
          </div>
        ` : ''}
      </div>
    </div>
  `;

  if (window.lucide) {
    window.lucide.createIcons();
  }
}

function formatRuntime(minutes) {
  if (!minutes || isNaN(minutes)) return null;
  const mins = parseInt(minutes, 10);
  const hrs = Math.floor(mins / 60);
  const remMins = mins % 60;
  if (hrs > 0) {
    return `${hrs} hr${hrs > 1 ? 's' : ''} ${remMins} min${remMins !== 1 ? 's' : ''}`;
  }
  return `${remMins} min${remMins !== 1 ? 's' : ''}`;
}

function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}
