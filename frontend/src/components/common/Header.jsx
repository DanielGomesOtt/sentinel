import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useTheme } from '../../context/ThemeContext';
import { RoleBadge } from './Badge';
import {
  ShieldAlert,
  Moon,
  Sun,
  LogOut,
  Menu,
  X,
  FileText,
  History,
  Terminal,
  Users,
  KeyRound,
} from 'lucide-react';

export function Header({ activeTab, setActiveTab }) {
  const { user, role, isAdmin, logout } = useAuth();
  const { isDark, toggleTheme } = useTheme();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navItems = [
    { id: 'incidents', label: 'Incidents', icon: FileText, show: true },
    { id: 'history', label: 'History', icon: History, show: true },
    { id: 'logs', label: 'Logs', icon: Terminal, show: true },
    { id: 'users', label: 'Users', icon: Users, show: isAdmin },
    { id: 'integrations', label: 'Integrations', icon: KeyRound, show: isAdmin },
  ];

  return (
    <header className="sticky top-0 z-40 bg-white/90 dark:bg-medium-bg-dark/90 backdrop-blur-md border-b border-medium-border dark:border-medium-border-dark transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          
          {/* Brand Logo & Name */}
          <div className="flex items-center gap-8">
            <button
              onClick={() => setActiveTab('incidents')}
              className="flex items-center gap-2.5 group focus:outline-none"
            >
              <div className="w-8 h-8 rounded-full bg-medium-text dark:bg-medium-text-dark text-white dark:text-black flex items-center justify-center font-bold transition-transform group-hover:scale-105">
                <ShieldAlert className="w-5 h-5 text-white dark:text-medium-bg-dark" />
              </div>
              <span className="font-serif font-bold text-xl tracking-tight text-medium-text dark:text-medium-text-dark">
                Sentinel
              </span>
            </button>

            {/* Desktop Navigation Links */}
            <nav className="hidden md:flex items-center space-x-1">
              {navItems.filter(item => item.show).map((item) => {
                const Icon = item.icon;
                const isActive = activeTab === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => setActiveTab(item.id)}
                    className={`flex items-center gap-2 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                      isActive
                        ? 'text-medium-green font-semibold bg-medium-green-light dark:bg-medium-green-dark-bg dark:text-emerald-400'
                        : 'text-medium-muted hover:text-medium-text dark:text-medium-muted-dark dark:hover:text-medium-text-dark hover:bg-gray-100 dark:hover:bg-gray-800/60'
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span>{item.label}</span>
                  </button>
                );
              })}
            </nav>
          </div>

          {/* Right Side Controls */}
          <div className="hidden md:flex items-center gap-4">
            {/* Dark Mode Toggle */}
            <button
              onClick={toggleTheme}
              className="p-2 rounded-full text-medium-muted dark:text-medium-muted-dark hover:text-medium-text dark:hover:text-medium-text-dark hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
              title={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
              aria-label="Toggle Theme"
            >
              {isDark ? <Sun className="w-5 h-5 text-amber-400" /> : <Moon className="w-5 h-5" />}
            </button>

            <div className="h-5 w-px bg-medium-border dark:bg-medium-border-dark" />

            {/* User Info & Role */}
            <div className="flex items-center gap-3">
              <div className="text-right">
                <div className="text-xs font-semibold text-medium-text dark:text-medium-text-dark">
                  {user?.name || 'User'}
                </div>
                <div className="text-[11px] text-medium-muted dark:text-medium-muted-dark">
                  {user?.email}
                </div>
              </div>
              <RoleBadge role={role} />
            </div>

            {/* Logout button */}
            <button
              onClick={logout}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-md text-xs font-medium text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-950/40 transition-colors ml-2"
              title="Sign out"
            >
              <LogOut className="w-4 h-4" />
              <span>Log out</span>
            </button>
          </div>

          {/* Mobile menu trigger */}
          <div className="flex md:hidden items-center gap-2">
            <button
              onClick={toggleTheme}
              className="p-2 rounded-full text-medium-muted dark:text-medium-muted-dark"
              aria-label="Toggle Theme"
            >
              {isDark ? <Sun className="w-5 h-5 text-amber-400" /> : <Moon className="w-5 h-5" />}
            </button>

            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 text-medium-text dark:text-medium-text-dark"
              aria-label="Open Menu"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-b border-medium-border dark:border-medium-border-dark bg-white dark:bg-medium-card-dark px-4 pt-2 pb-4 space-y-2">
          <div className="py-2 border-b border-medium-border dark:border-medium-border-dark flex items-center justify-between">
            <div>
              <p className="font-semibold text-sm text-medium-text dark:text-medium-text-dark">{user?.name}</p>
              <p className="text-xs text-medium-muted">{user?.email}</p>
            </div>
            <RoleBadge role={role} />
          </div>

          {navItems.filter(item => item.show).map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => {
                  setActiveTab(item.id);
                  setMobileMenuOpen(false);
                }}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-left transition-colors ${
                  isActive
                    ? 'text-medium-green font-semibold bg-medium-green-light dark:bg-medium-green-dark-bg dark:text-emerald-400'
                    : 'text-medium-muted dark:text-medium-muted-dark'
                }`}
              >
                <Icon className="w-5 h-5" />
                <span>{item.label}</span>
              </button>
            );
          })}

          <button
            onClick={logout}
            className="w-full flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-950/40 transition-colors mt-2"
          >
            <LogOut className="w-5 h-5" />
            <span>Sign out</span>
          </button>
        </div>
      )}
    </header>
  );
}
