import React from 'react';

export function SeverityBadge({ severity }) {
  const sev = (severity || '').toUpperCase();

  const styles = {
    CRITICAL: 'bg-red-50 text-red-700 border-red-200 dark:bg-red-950/50 dark:text-red-300 dark:border-red-800',
    HIGH: 'bg-orange-50 text-orange-700 border-orange-200 dark:bg-orange-950/50 dark:text-orange-300 dark:border-orange-800',
    MEDIUM: 'bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-950/50 dark:text-amber-300 dark:border-amber-800',
    LOW: 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950/50 dark:text-emerald-300 dark:border-emerald-800',
  };

  const defaultStyle = 'bg-gray-50 text-gray-700 border-gray-200 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-700';

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${styles[sev] || defaultStyle}`}>
      <span className="w-1.5 h-1.5 mr-1.5 rounded-full bg-current opacity-75"></span>
      {sev}
    </span>
  );
}

export function StatusBadge({ status }) {
  const st = (status || '').toUpperCase();

  const styles = {
    OPEN: 'bg-blue-50 text-blue-700 border-blue-200 dark:bg-blue-950/50 dark:text-blue-300 dark:border-blue-800',
    UNDER_REVIEW: 'bg-purple-50 text-purple-700 border-purple-200 dark:bg-purple-950/50 dark:text-purple-300 dark:border-purple-800',
    IN_CORRECTION: 'bg-indigo-50 text-indigo-700 border-indigo-200 dark:bg-indigo-950/50 dark:text-indigo-300 dark:border-indigo-800',
    RESOLVED: 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950/50 dark:text-emerald-300 dark:border-emerald-800',
    CLOSED: 'bg-gray-100 text-gray-700 border-gray-300 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-700',
  };

  const defaultStyle = 'bg-gray-50 text-gray-700 border-gray-200 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-700';

  const labels = {
    OPEN: 'Open',
    UNDER_REVIEW: 'Under Review',
    IN_CORRECTION: 'In Correction',
    RESOLVED: 'Resolved',
    CLOSED: 'Closed',
  };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${styles[st] || defaultStyle}`}>
      {labels[st] || st}
    </span>
  );
}

export function LogLevelBadge({ level }) {
  const lvl = (level || '').toUpperCase();

  const styles = {
    INFO: 'bg-sky-50 text-sky-700 border-sky-200 dark:bg-sky-950/50 dark:text-sky-300 dark:border-sky-800',
    WARN: 'bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-950/50 dark:text-amber-300 dark:border-amber-800',
    ERROR: 'bg-rose-50 text-rose-700 border-rose-200 dark:bg-rose-950/50 dark:text-rose-300 dark:border-rose-800',
  };

  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-mono font-semibold border ${styles[lvl] || 'bg-gray-100 text-gray-700'}`}>
      {lvl}
    </span>
  );
}

export function RoleBadge({ role }) {
  const r = (role || '').toUpperCase();

  const styles = {
    ADMIN: 'bg-purple-100 text-purple-800 dark:bg-purple-900/60 dark:text-purple-300',
    TECH: 'bg-blue-100 text-blue-800 dark:bg-blue-900/60 dark:text-blue-300',
    USER: 'bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-300',
    SYSTEM: 'bg-teal-100 text-teal-800 dark:bg-teal-900/60 dark:text-teal-300',
  };

  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-semibold uppercase tracking-wider ${styles[r] || 'bg-gray-100 text-gray-800'}`}>
      {r}
    </span>
  );
}
