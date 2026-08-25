/**
 * Security & Input Validation Utilities for Sentinel
 * Handles input sanitization, type checks, XSS prevention, and parameter validation.
 */

// Escape HTML special characters to prevent XSS attacks
export function escapeHtml(str) {
  if (typeof str !== 'string') return str;
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

// Sanitize user inputs by trimming and stripping dangerous HTML/script tags
export function sanitizeString(str) {
  if (str === null || str === undefined) return '';
  if (typeof str !== 'string') str = String(str);
  
  // Strip control characters & dangerous HTML tags
  let cleaned = str
    .trim()
    .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
    .replace(/javascript:/gi, '')
    .replace(/on\w+=/gi, '');

  return cleaned;
}

// Validate email format
export function validateEmail(email) {
  if (!email || typeof email !== 'string') return { isValid: false, message: 'Email is required.' };
  const sanitized = email.trim();
  if (sanitized.length > 254) return { isValid: false, message: 'Email is too long.' };
  
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!emailRegex.test(sanitized)) {
    return { isValid: false, message: 'Please enter a valid email address (e.g. user@company.com).' };
  }
  return { isValid: true, sanitizedValue: sanitized };
}

// Validate password rules
export function validatePassword(password, fieldName = 'Password') {
  if (!password || typeof password !== 'string') {
    return { isValid: false, message: `${fieldName} is required.` };
  }
  if (password.length < 6) {
    return { isValid: false, message: `${fieldName} must be at least 6 characters long.` };
  }
  if (password.length > 100) {
    return { isValid: false, message: `${fieldName} cannot exceed 100 characters.` };
  }
  return { isValid: true, sanitizedValue: password };
}

// Validate string field with length constraints
export function validateText(value, name, minLen = 1, maxLen = 255, required = true) {
  if (value === null || value === undefined || String(value).trim() === '') {
    if (required) {
      return { isValid: false, message: `${name} is a required field.` };
    }
    return { isValid: true, sanitizedValue: '' };
  }

  const sanitized = sanitizeString(value);
  if (sanitized.length < minLen) {
    return { isValid: false, message: `${name} must be at least ${minLen} character(s).` };
  }
  if (sanitized.length > maxLen) {
    return { isValid: false, message: `${name} cannot exceed ${maxLen} characters.` };
  }
  return { isValid: true, sanitizedValue: sanitized };
}

// Validate numeric ID parameter
export function validateId(id, name = 'ID') {
  if (id === null || id === undefined || id === '') {
    return { isValid: false, message: `${name} is required.` };
  }
  const num = Number(id);
  if (!Number.isInteger(num) || num <= 0) {
    return { isValid: false, message: `${name} must be a valid positive integer.` };
  }
  return { isValid: true, sanitizedValue: num };
}

// Validate enum value against allowed options
export function validateEnum(value, allowedValues, name = 'Field') {
  if (!value) return { isValid: true, sanitizedValue: '' };
  const sanitized = sanitizeString(value).toUpperCase();
  if (!allowedValues.includes(sanitized)) {
    return { isValid: false, message: `${name} contains an invalid value.` };
  }
  return { isValid: true, sanitizedValue: sanitized };
}

// Clean and sanitize query parameters for GET requests
export function sanitizeQueryParams(params) {
  const cleaned = {};
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== null && value !== '') {
      if (typeof value === 'boolean') {
        cleaned[key] = value;
      } else if (typeof value === 'number') {
        cleaned[key] = value;
      } else if (typeof value === 'string') {
        const trimmed = sanitizeString(value);
        if (trimmed !== '') {
          cleaned[key] = trimmed;
        }
      }
    }
  }
  return cleaned;
}
