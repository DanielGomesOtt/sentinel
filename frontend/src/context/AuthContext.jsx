import React, { createContext, useContext, useState, useEffect } from 'react';
import { apiClient, setUnauthorizedHandler } from '../services/apiClient';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem('sentinel_user');
      return saved ? JSON.parse(saved) : null;
    } catch (e) {
      return null;
    }
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const logout = () => {
    localStorage.removeItem('sentinel_user');
    setUser(null);
    setError(null);
  };

  useEffect(() => {
    setUnauthorizedHandler(logout);
  }, []);

  const saveUserSession = (userData) => {
    setUser(userData);
    localStorage.setItem('sentinel_user', JSON.stringify(userData));
  };

  // Login handler
  const login = async (email, password) => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiClient.post('/v1/auth/login', { email, password });
      saveUserSession(data);
      setLoading(false);
      return data;
    } catch (err) {
      setLoading(false);
      setError(err.message);
      throw err;
    }
  };

  // Register initial root user
  const registerRoot = async (name, email, password, organizationName) => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiClient.post('/v1/auth/register', {
        name,
        email,
        password,
        organizationName,
      });
      saveUserSession(data);
      setLoading(false);
      return data;
    } catch (err) {
      setLoading(false);
      setError(err.message);
      throw err;
    }
  };

  // Request password reset code
  const requestResetCode = async (email) => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.post('/v1/auth/forgot_password/reset_code', { email });
      setLoading(false);
      return res;
    } catch (err) {
      setLoading(false);
      setError(err.message);
      throw err;
    }
  };

  // Reset password using code
  const resetPassword = async (code, email, newPassword) => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.post('/v1/auth/forgot_password/reset_password', {
        code,
        email,
        newPassword,
      });
      setLoading(false);
      return res;
    } catch (err) {
      setLoading(false);
      setError(err.message);
      throw err;
    }
  };

  // Role permissions
  const role = user?.role ? user.role.toUpperCase() : null;
  const isAdmin = role === 'ADMIN';
  const isTech = role === 'ADMIN' || role === 'TECH';
  const isAuthenticated = !!user && !!user.token;

  return (
    <AuthContext.Provider
      value={{
        user,
        role,
        isAdmin,
        isTech,
        isAuthenticated,
        loading,
        error,
        setError,
        login,
        registerRoot,
        requestResetCode,
        resetPassword,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
