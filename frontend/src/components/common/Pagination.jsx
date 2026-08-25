import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

export function Pagination({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  onSizeChange,
}) {
  if (totalElements === 0 || totalPages === 0) return null;

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-4 py-4 px-2 border-t border-medium-border dark:border-medium-border-dark text-sm">
      <div className="flex items-center gap-4 text-medium-muted dark:text-medium-muted-dark text-xs sm:text-sm">
        <span>
          Total of <strong className="text-medium-text dark:text-medium-text-dark font-medium">{totalElements}</strong> records
        </span>

        {onSizeChange && (
          <div className="flex items-center gap-1.5">
            <label htmlFor="pageSizeSelect" className="hidden sm:inline">Show:</label>
            <select
              id="pageSizeSelect"
              value={pageSize}
              onChange={(e) => onSizeChange(Number(e.target.value))}
              className="bg-transparent border border-medium-border dark:border-medium-border-dark rounded px-2 py-1 text-xs text-medium-text dark:text-medium-text-dark focus:outline-none focus:ring-1 focus:ring-medium-green"
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
            </select>
          </div>
        )}
      </div>

      <div className="flex items-center gap-2">
        <span className="text-xs text-medium-muted dark:text-medium-muted-dark mr-2">
          Page <span className="font-semibold text-medium-text dark:text-medium-text-dark">{currentPage + 1}</span> of <span className="font-semibold text-medium-text dark:text-medium-text-dark">{totalPages}</span>
        </span>

        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0}
          className="inline-flex items-center justify-center p-1.5 rounded-md border border-medium-border dark:border-medium-border-dark text-medium-text dark:text-medium-text-dark disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
          title="Previous Page"
        >
          <ChevronLeft className="w-4 h-4" />
        </button>

        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage >= totalPages - 1}
          className="inline-flex items-center justify-center p-1.5 rounded-md border border-medium-border dark:border-medium-border-dark text-medium-text dark:text-medium-text-dark disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
          title="Next Page"
        >
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
}
