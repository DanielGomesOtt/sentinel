import React from 'react';

export function Input({
  label,
  id,
  type = 'text',
  value,
  onChange,
  error,
  placeholder,
  required = false,
  maxLength,
  disabled = false,
  helpText,
  icon: Icon,
  className = '',
  rows,
}) {
  const currentLen = typeof value === 'string' ? value.length : 0;
  const isTextArea = type === 'textarea';

  return (
    <div className={`w-full flex flex-col gap-1 ${className}`}>
      {label && (
        <div className="flex items-center justify-between text-xs font-medium text-medium-text dark:text-medium-text-dark">
          <label htmlFor={id}>
            {label} {required && <span className="text-red-500">*</span>}
          </label>
          {maxLength && (
            <span className={`text-[10px] ${currentLen >= maxLength ? 'text-red-500 font-bold' : 'text-medium-muted'}`}>
              {currentLen}/{maxLength}
            </span>
          )}
        </div>
      )}

      <div className="relative flex items-center">
        {Icon && !isTextArea && (
          <div className="absolute left-3 text-medium-muted pointer-events-none">
            <Icon className="w-4 h-4" />
          </div>
        )}

        {isTextArea ? (
          <textarea
            id={id}
            value={value}
            onChange={onChange}
            placeholder={placeholder}
            maxLength={maxLength}
            disabled={disabled}
            rows={rows || 3}
            className={`w-full rounded-md border px-3 py-2 text-sm bg-white dark:bg-medium-surface-dark text-medium-text dark:text-medium-text-dark placeholder-medium-muted/60 focus:outline-none transition-all duration-150 ${
              error
                ? 'border-red-500 focus:ring-1 focus:ring-red-500'
                : 'border-medium-border dark:border-medium-border-dark focus:border-medium-green focus:ring-1 focus:ring-medium-green'
            } ${disabled ? 'opacity-60 cursor-not-allowed bg-gray-50 dark:bg-gray-900' : ''}`}
          />
        ) : (
          <input
            id={id}
            type={type}
            value={value}
            onChange={onChange}
            placeholder={placeholder}
            maxLength={maxLength}
            disabled={disabled}
            className={`w-full rounded-md border px-3 py-2 text-sm bg-white dark:bg-medium-surface-dark text-medium-text dark:text-medium-text-dark placeholder-medium-muted/60 focus:outline-none transition-all duration-150 ${
              Icon ? 'pl-9' : ''
            } ${
              error
                ? 'border-red-500 focus:ring-1 focus:ring-red-500'
                : 'border-medium-border dark:border-medium-border-dark focus:border-medium-green focus:ring-1 focus:ring-medium-green'
            } ${disabled ? 'opacity-60 cursor-not-allowed bg-gray-50 dark:bg-gray-900' : ''}`}
          />
        )}
      </div>

      {error ? (
        <span className="text-xs text-red-600 dark:text-red-400 font-medium animate-fadeIn">
          {error}
        </span>
      ) : helpText ? (
        <span className="text-[11px] text-medium-muted dark:text-medium-muted-dark">
          {helpText}
        </span>
      ) : null}
    </div>
  );
}

export function Select({
  label,
  id,
  value,
  onChange,
  options = [],
  error,
  required = false,
  disabled = false,
  placeholder = 'Select an option',
  className = '',
}) {
  return (
    <div className={`w-full flex flex-col gap-1 ${className}`}>
      {label && (
        <label htmlFor={id} className="text-xs font-medium text-medium-text dark:text-medium-text-dark">
          {label} {required && <span className="text-red-500">*</span>}
        </label>
      )}

      <select
        id={id}
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full rounded-md border px-3 py-2 text-sm bg-white dark:bg-medium-surface-dark text-medium-text dark:text-medium-text-dark focus:outline-none transition-all duration-150 ${
          error
            ? 'border-red-500 focus:ring-1 focus:ring-red-500'
            : 'border-medium-border dark:border-medium-border-dark focus:border-medium-green focus:ring-1 focus:ring-medium-green'
        } ${disabled ? 'opacity-60 cursor-not-allowed bg-gray-50 dark:bg-gray-900' : ''}`}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>

      {error && (
        <span className="text-xs text-red-600 dark:text-red-400 font-medium animate-fadeIn">
          {error}
        </span>
      )}
    </div>
  );
}
