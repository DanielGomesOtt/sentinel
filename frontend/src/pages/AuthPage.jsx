import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { Input } from '../components/common/Input';
import { validateEmail, validatePassword, validateText } from '../utils/security';
import { ShieldAlert, Moon, Sun, ArrowRight, CheckCircle2, AlertCircle, KeyRound, UserPlus, LogIn } from 'lucide-react';

export function AuthPage() {
  // Modes: 'login' | 'register' | 'forgot' | 'reset'
  const [mode, setMode] = useState('login');

  const { login, registerRoot, requestResetCode, resetPassword, loading, error, setError } = useAuth();
  const { isDark, toggleTheme } = useTheme();

  // Form states
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [organizationName, setOrganizationName] = useState('');
  const [code, setCode] = useState('');
  const [newPassword, setNewPassword] = useState('');

  // Field validation errors
  const [fieldErrors, setFieldErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState('');

  const clearState = () => {
    setFieldErrors({});
    setError(null);
    setSuccessMessage('');
  };

  const handleModeChange = (newMode) => {
    clearState();
    setMode(newMode);
  };

  // 1. Submit Login
  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    clearState();

    const emailVal = validateEmail(email);
    const passVal = validatePassword(password);

    const errors = {};
    if (!emailVal.isValid) errors.email = emailVal.message;
    if (!passVal.isValid) errors.password = passVal.message;

    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    try {
      await login(emailVal.sanitizedValue, password);
    } catch (err) {
      // error handled in context
    }
  };

  // 2. Submit Register Root
  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    clearState();

    const nameVal = validateText(name, 'Full Name', 2, 100);
    const emailVal = validateEmail(email);
    const passVal = validatePassword(password);
    const orgVal = validateText(organizationName, 'Organization Name', 2, 100);

    const errors = {};
    if (!nameVal.isValid) errors.name = nameVal.message;
    if (!emailVal.isValid) errors.email = emailVal.message;
    if (!passVal.isValid) errors.password = passVal.message;
    if (!orgVal.isValid) errors.organizationName = orgVal.message;

    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    try {
      await registerRoot(
        nameVal.sanitizedValue,
        emailVal.sanitizedValue,
        password,
        orgVal.sanitizedValue
      );
    } catch (err) {
      // error handled in context
    }
  };

  // 3. Submit Request Reset Code
  const handleForgotSubmit = async (e) => {
    e.preventDefault();
    clearState();

    const emailVal = validateEmail(email);
    if (!emailVal.isValid) {
      setFieldErrors({ email: emailVal.message });
      return;
    }

    try {
      const res = await requestResetCode(emailVal.sanitizedValue);
      setSuccessMessage(res?.message || 'Verification code sent if email is registered.');
    } catch (err) {
      // error handled in context
    }
  };

  // 4. Submit Reset Password
  const handleResetSubmit = async (e) => {
    e.preventDefault();
    clearState();

    const codeVal = validateText(code, 'Verification Code', 3, 50);
    const emailVal = validateEmail(email);
    const passVal = validatePassword(newPassword, 'New Password');

    const errors = {};
    if (!codeVal.isValid) errors.code = codeVal.message;
    if (!emailVal.isValid) errors.email = emailVal.message;
    if (!passVal.isValid) errors.newPassword = passVal.message;

    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    try {
      const res = await resetPassword(codeVal.sanitizedValue, emailVal.sanitizedValue, newPassword);
      setSuccessMessage(res?.message || 'Password successfully reset! Please log in.');
      setTimeout(() => handleModeChange('login'), 2500);
    } catch (err) {
      // error handled in context
    }
  };

  return (
    <div className="min-h-screen flex flex-col justify-between bg-medium-bg dark:bg-medium-bg-dark transition-colors px-4 py-8">
      {/* Top Header Controls */}
      <div className="w-full max-w-4xl mx-auto flex items-center justify-between">
        <div className="flex items-center gap-2.5">
          <div className="w-9 h-9 rounded-full bg-medium-text dark:bg-medium-text-dark text-white dark:text-black flex items-center justify-center font-bold">
            <ShieldAlert className="w-5 h-5 text-white dark:text-medium-bg-dark" />
          </div>
          <span className="font-serif font-bold text-2xl tracking-tight text-medium-text dark:text-medium-text-dark">
            Sentinel
          </span>
        </div>

        <button
          onClick={toggleTheme}
          className="p-2 rounded-full text-medium-muted hover:text-medium-text dark:text-medium-muted-dark dark:hover:text-medium-text-dark transition-colors"
          title="Toggle Theme"
        >
          {isDark ? <Sun className="w-5 h-5 text-amber-400" /> : <Moon className="w-5 h-5" />}
        </button>
      </div>

      {/* Main Authentication Form Container */}
      <div className="w-full max-w-md mx-auto my-auto py-8">
        <div className="text-center mb-8">
          <h1 className="font-serif text-3xl font-bold tracking-tight text-medium-text dark:text-medium-text-dark">
            {mode === 'login' && 'Welcome Back'}
            {mode === 'register' && 'Create Root Organization'}
            {mode === 'forgot' && 'Reset Password'}
            {mode === 'reset' && 'Set New Password'}
          </h1>
          <p className="mt-2 text-sm text-medium-muted dark:text-medium-muted-dark">
            {mode === 'login' && 'Sign in to access the incident management platform.'}
            {mode === 'register' && 'Initial system setup for root organization.'}
            {mode === 'forgot' && 'Request a verification code sent to your email.'}
            {mode === 'reset' && 'Enter your verification code and new password.'}
          </p>
        </div>

        {/* Global Feedback Banner */}
        {error && (
          <div className="mb-6 p-4 rounded-md bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-800 flex items-start gap-3 text-red-700 dark:text-red-300 text-sm">
            <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        )}

        {successMessage && (
          <div className="mb-6 p-4 rounded-md bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 flex items-start gap-3 text-emerald-700 dark:text-emerald-300 text-sm">
            <CheckCircle2 className="w-5 h-5 flex-shrink-0 mt-0.5" />
            <span>{successMessage}</span>
          </div>
        )}

        {/* LOGIN FORM */}
        {mode === 'login' && (
          <form onSubmit={handleLoginSubmit} className="space-y-4">
            <Input
              label="Email"
              id="loginEmail"
              type="email"
              placeholder="user@company.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={fieldErrors.email}
              required
            />

            <Input
              label="Password"
              id="loginPassword"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              error={fieldErrors.password}
              required
            />

            <div className="flex justify-end text-xs">
              <button
                type="button"
                onClick={() => handleModeChange('forgot')}
                className="text-medium-muted hover:text-medium-text dark:text-medium-muted-dark dark:hover:text-medium-text-dark transition-colors"
              >
                Forgot password?
              </button>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full mt-2 py-2.5 px-4 rounded-full bg-medium-green hover:bg-medium-green-hover text-white font-medium text-sm transition-all duration-150 flex items-center justify-center gap-2 shadow-sm disabled:opacity-50"
            >
              {loading ? 'Signing in...' : 'Sign In'}
              {!loading && <ArrowRight className="w-4 h-4" />}
            </button>
          </form>
        )}

        {/* REGISTER ROOT FORM */}
        {mode === 'register' && (
          <form onSubmit={handleRegisterSubmit} className="space-y-4">
            <Input
              label="Full Name"
              id="regName"
              placeholder="Root Administrator"
              value={name}
              onChange={(e) => setName(e.target.value)}
              error={fieldErrors.name}
              required
              maxLength={100}
            />

            <Input
              label="Corporate Email"
              id="regEmail"
              type="email"
              placeholder="admin@company.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={fieldErrors.email}
              required
            />

            <Input
              label="Organization Name"
              id="regOrg"
              placeholder="Example Corp"
              value={organizationName}
              onChange={(e) => setOrganizationName(e.target.value)}
              error={fieldErrors.organizationName}
              required
              maxLength={100}
            />

            <Input
              label="Password"
              id="regPassword"
              type="password"
              placeholder="Minimum 6 characters"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              error={fieldErrors.password}
              required
            />

            <button
              type="submit"
              disabled={loading}
              className="w-full mt-2 py-2.5 px-4 rounded-full bg-medium-green hover:bg-medium-green-hover text-white font-medium text-sm transition-all duration-150 flex items-center justify-center gap-2 shadow-sm disabled:opacity-50"
            >
              {loading ? 'Creating...' : 'Create Root Organization'}
              {!loading && <UserPlus className="w-4 h-4" />}
            </button>
          </form>
        )}

        {/* FORGOT PASSWORD FORM */}
        {mode === 'forgot' && (
          <form onSubmit={handleForgotSubmit} className="space-y-4">
            <Input
              label="Registered Email"
              id="forgotEmail"
              type="email"
              placeholder="user@company.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={fieldErrors.email}
              required
            />

            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 px-4 rounded-full bg-medium-green hover:bg-medium-green-hover text-white font-medium text-sm transition-all duration-150 flex items-center justify-center gap-2 shadow-sm disabled:opacity-50"
            >
              {loading ? 'Sending...' : 'Request Verification Code'}
            </button>

            <div className="text-center pt-2">
              <button
                type="button"
                onClick={() => handleModeChange('reset')}
                className="text-xs text-medium-green hover:underline font-medium"
              >
                Already have a code? Reset password here.
              </button>
            </div>
          </form>
        )}

        {/* RESET PASSWORD FORM */}
        {mode === 'reset' && (
          <form onSubmit={handleResetSubmit} className="space-y-4">
            <Input
              label="Email"
              id="resetEmail"
              type="email"
              placeholder="user@company.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={fieldErrors.email}
              required
            />

            <Input
              label="Verification Code"
              id="resetCode"
              placeholder="Code received by email"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              error={fieldErrors.code}
              required
            />

            <Input
              label="New Password"
              id="resetNewPassword"
              type="password"
              placeholder="Minimum 6 characters"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              error={fieldErrors.newPassword}
              required
            />

            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 px-4 rounded-full bg-medium-green hover:bg-medium-green-hover text-white font-medium text-sm transition-all duration-150 flex items-center justify-center gap-2 shadow-sm disabled:opacity-50"
            >
              {loading ? 'Resetting...' : 'Update Password'}
              {!loading && <KeyRound className="w-4 h-4" />}
            </button>
          </form>
        )}

        {/* Secondary Navigation Footer Links */}
        <div className="mt-8 pt-6 border-t border-medium-border dark:border-medium-border-dark flex items-center justify-center gap-4 text-xs text-medium-muted dark:text-medium-muted-dark">
          {mode !== 'login' && (
            <button
              onClick={() => handleModeChange('login')}
              className="hover:text-medium-text dark:hover:text-medium-text-dark font-medium transition-colors"
            >
              Back to Login
            </button>
          )}

          {mode !== 'register' && (
            <>
              {mode !== 'login' && <span>•</span>}
              <button
                onClick={() => handleModeChange('register')}
                className="hover:text-medium-text dark:hover:text-medium-text-dark font-medium transition-colors"
              >
                Initial Setup / Register Root
              </button>
            </>
          )}
        </div>
      </div>

      {/* Minimalist Footer */}
      <footer className="text-center text-xs text-medium-muted dark:text-medium-muted-dark py-4">
        Sentinel Incident Management Platform &copy; {new Date().getFullYear()}
      </footer>
    </div>
  );
}
