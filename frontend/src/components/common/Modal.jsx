import React, { useEffect } from 'react';
import { X } from 'lucide-react';

export function Modal({ isOpen, onClose, title, children, maxWidth = 'max-w-xl' }) {
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => {
      document.body.style.overflow = 'unset';
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-black/50 backdrop-blur-xs transition-opacity"
        onClick={onClose}
      />

      {/* Dialog container */}
      <div className="flex min-h-full items-center justify-center p-4 text-center sm:p-0">
        <div className={`relative transform overflow-hidden rounded-lg bg-white dark:bg-medium-card-dark text-left shadow-xl transition-all w-full ${maxWidth} border border-medium-border dark:border-medium-border-dark my-8`}>
          
          {/* Header */}
          <div className="flex items-center justify-between px-6 py-4 border-b border-medium-border dark:border-medium-border-dark">
            <h3 className="text-lg font-serif font-semibold text-medium-text dark:text-medium-text-dark">
              {title}
            </h3>
            <button
              onClick={onClose}
              className="rounded-full p-1 text-medium-muted hover:text-medium-text dark:hover:text-medium-text-dark hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
              aria-label="Close"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Body */}
          <div className="px-6 py-5 max-h-[80vh] overflow-y-auto">
            {children}
          </div>
        </div>
      </div>
    </div>
  );
}
