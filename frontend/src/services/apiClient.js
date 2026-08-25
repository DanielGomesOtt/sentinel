/**
 * Native Fetch API Client for Sentinel Backend
 * Handles authorization headers, error responses, JSON serialization, and PDF downloads.
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

let onUnauthorizedCallback = null;

export function setUnauthorizedHandler(callback) {
  onUnauthorizedCallback = callback;
}

// Get auth token from localStorage
function getAuthToken() {
  try {
    const userJson = localStorage.getItem('sentinel_user');
    if (!userJson) return null;
    const user = JSON.parse(userJson);
    return user?.token || null;
  } catch (e) {
    return null;
  }
}

// Build headers with JWT authorization
function getHeaders(customHeaders = {}) {
  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    ...customHeaders,
  };

  const token = getAuthToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  return headers;
}

// Core HTTP request wrapper using native fetch
async function request(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), options.timeout || 30000);

  try {
    const response = await fetch(url, {
      ...options,
      headers: getHeaders(options.headers),
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    // Handle HTTP status errors
    if (!response.ok) {
      if (response.status === 401) {
        if (onUnauthorizedCallback) {
          onUnauthorizedCallback();
        }
        throw new Error('Sessão expirada ou não autorizada. Faça login novamente.');
      }

      if (response.status === 403) {
        throw new Error('Você não tem permissão para realizar esta ação.');
      }

      let errorMessage = `Erro na requisição (Código: ${response.status})`;
      try {
        const errorData = await response.json();
        if (errorData.message) {
          errorMessage = errorData.message;
        } else if (errorData.error) {
          errorMessage = errorData.error;
        } else if (Array.isArray(errorData.errors)) {
          errorMessage = errorData.errors.map(e => e.defaultMessage || e.message || e).join(', ');
        }
      } catch (e) {
        // If JSON parsing fails, fallback to statusText
        if (response.statusText) errorMessage = response.statusText;
      }

      throw new Error(errorMessage);
    }

    // Return empty object for 204 No Content
    if (response.status === 204) {
      return null;
    }

    return await response.json();
  } catch (err) {
    clearTimeout(timeoutId);
    if (err.name === 'AbortError') {
      throw new Error('Tempo limite da requisição atingido (Timeout). Verifique sua conexão.');
    }
    throw err;
  }
}

// Helper to download PDF binary data
export async function downloadPdf(endpoint, queryParams = {}, filename = 'relatorio.pdf') {
  const queryString = new URLSearchParams(queryParams).toString();
  const fullUrl = `${API_BASE_URL}${endpoint}${queryString ? `?${queryString}` : ''}`;
  
  const token = getAuthToken();
  const headers = {};
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(fullUrl, { method: 'GET', headers });

  if (!response.ok) {
    if (response.status === 401) {
      if (onUnauthorizedCallback) onUnauthorizedCallback();
      throw new Error('Sessão expirada. Faça login novamente.');
    }
    throw new Error(`Falha ao gerar PDF. Status: ${response.status}`);
  }

  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}

export const apiClient = {
  get: (endpoint, queryParams = {}) => {
    const queryString = new URLSearchParams(queryParams).toString();
    const fullEndpoint = `${endpoint}${queryString ? `?${queryString}` : ''}`;
    return request(fullEndpoint, { method: 'GET' });
  },

  post: (endpoint, body) => {
    return request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body),
    });
  },

  put: (endpoint, body) => {
    return request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body),
    });
  },

  delete: (endpoint) => {
    return request(endpoint, { method: 'DELETE' });
  },

  downloadPdf,
};
