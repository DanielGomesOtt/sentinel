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

    const nameVal = validateText(name, 'Nome Completo', 2, 100);
    const emailVal = validateEmail(email);
    const passVal = validatePassword(password);
    const orgVal = validateText(organizationName, 'Nome da Organização', 2, 100);

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
      setSuccessMessage(res?.message || 'Código enviado se o e-mail estiver cadastrado.');
    } catch (err) {
      // error handled in context
    }
  };

  // 4. Submit Reset Password
  const handleResetSubmit = async (e) => {
    e.preventDefault();
    clearState();

    const codeVal = validateText(code, 'Código de verificação', 3, 50);
    const emailVal = validateEmail(email);
    const passVal = validatePassword(newPassword, 'Nova Senha');

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
      setSuccessMessage(res?.message || 'Senha redefinida com sucesso! Faça login.');
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
          title="Alternar Tema"
        >
          {isDark ? <Sun className="w-5 h-5 text-amber-400" /> : <Moon className="w-5 h-5" />}
        </button>
      </div>

      {/* Main Authentication Form Container */}
      <div className="w-full max-w-md mx-auto my-auto py-8">
        <div className="text-center mb-8">
          <h1 className="font-serif text-3xl font-bold tracking-tight text-medium-text dark:text-medium-text-dark">
            {mode === 'login' && 'Bem-vindo de volta'}
            {mode === 'register' && 'Criar Organização Root'}
            {mode === 'forgot' && 'Recuperar Senha'}
            {mode === 'reset' && 'Redefinir Senha'}
          </h1>
          <p className="mt-2 text-sm text-medium-muted dark:text-medium-muted-dark">
            {mode === 'login' && 'Acesse a plataforma de gerenciamento de incidentes.'}
            {mode === 'register' && 'Configuração inicial da organização root do Sentinel.'}
            {mode === 'forgot' && 'Solicite um código de verificação para o seu e-mail.'}
            {mode === 'reset' && 'Insira o código recebido e sua nova senha.'}
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
              label="E-mail"
              id="loginEmail"
              type="email"
              placeholder="seu.email@empresa.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={fieldErrors.email}
              required
            />

            <Input
              label="Senha"
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
                Esqueceu a senha?
              </button>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full mt-2 py-2.5 px-4 rounded-full bg-medium-green hover:bg-medium-green-hover text-white font-medium text-sm transition-all duration-150 flex items-center justify-center gap-2 shadow-sm disabled:opacity-50"
            >
              {loading ? 'Entrando...' : 'Entrar na Conta'}
              {!loading && <ArrowRight className="w-4 h-4" />}
            </button>
          </form>
        )}

        {/* REGISTER ROOT FORM */}
        {mode === 'register' && (
          <form onSubmit={handleRegisterSubmit} className="space-y-4">
            <Input
              label="Nome Completo"
              id="regName"
              placeholder="Administrador Root"
              value={name}
              onChange={(e) => setName(e.target.value)}
              error={fieldErrors.name}
              required
              maxLength={100}
            />

            <Input
              label="E-mail Corporativo"
              id="regEmail"
              type="email"
              placeholder="admin@empresa.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={fieldErrors.email}
              required
            />

            <Input
              label="Nome da Organização"
              id="regOrg"
              placeholder="Minha Empresa S.A."
              value={organizationName}
              onChange={(e) => setOrganizationName(e.target.value)}
              error={fieldErrors.organizationName}
              required
              maxLength={100}
            />

            <Input
              label="Senha de Acesso"
              id="regPassword"
              type="password"
              placeholder="Mínimo 6 caracteres"
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
              {loading ? 'Cadastrando...' : 'Criar Organização Root'}
              {!loading && <UserPlus className="w-4 h-4" />}
            </button>
          </form>
        )}

        {/* FORGOT PASSWORD FORM */}
        {mode === 'forgot' && (
          <form onSubmit={handleForgotSubmit} className="space-y-4">
            <Input
              label="E-mail Cadastrado"
              id="forgotEmail"
              type="email"
              placeholder="seu.email@empresa.com"
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
              {loading ? 'Enviando...' : 'Solicitar Código de Redefinição'}
            </button>

            <div className="text-center pt-2">
              <button
                type="button"
                onClick={() => handleModeChange('reset')}
                className="text-xs text-medium-green hover:underline font-medium"
              >
                Já possui um código? Redefinir senha aqui.
              </button>
            </div>
          </form>
        )}

        {/* RESET PASSWORD FORM */}
        {mode === 'reset' && (
          <form onSubmit={handleResetSubmit} className="space-y-4">
            <Input
              label="E-mail"
              id="resetEmail"
              type="email"
              placeholder="seu.email@empresa.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={fieldErrors.email}
              required
            />

            <Input
              label="Código de Verificação"
              id="resetCode"
              placeholder="Código recebido por e-mail"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              error={fieldErrors.code}
              required
            />

            <Input
              label="Nova Senha"
              id="resetNewPassword"
              type="password"
              placeholder="Mínimo 6 caracteres"
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
              {loading ? 'Redefinindo...' : 'Atualizar Senha'}
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
              Voltar ao Login
            </button>
          )}

          {mode !== 'register' && (
            <>
              {mode !== 'login' && <span>•</span>}
              <button
                onClick={() => handleModeChange('register')}
                className="hover:text-medium-text dark:hover:text-medium-text-dark font-medium transition-colors"
              >
                Primeiro Acesso / Cadastrar Root
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
