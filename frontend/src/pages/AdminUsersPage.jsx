import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../services/apiClient';
import { Input, Select } from '../components/common/Input';
import { validateEmail, validatePassword, validateText, validateId } from '../utils/security';
import { Users, UserPlus, CheckCircle2, AlertCircle, Shield } from 'lucide-react';

const ROLES = [
  { value: 'ADMIN', label: 'ADMIN - Full administrative access' },
  { value: 'TECH', label: 'TECH - Update and resolve incidents' },
  { value: 'USER', label: 'USER - View and search only' },
];

export function AdminUsersPage() {
  const { user } = useAuth();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [orgId, setOrgId] = useState('1');
  const [role, setRole] = useState('TECH');

  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [success, setSuccess] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    setSuccess(null);

    const nameVal = validateText(name, 'Full Name', 2, 100);
    const emailVal = validateEmail(email);
    const passVal = validatePassword(password);
    const orgVal = validateId(orgId, 'Organization ID');

    const validationErrors = {};
    if (!nameVal.isValid) validationErrors.name = nameVal.message;
    if (!emailVal.isValid) validationErrors.email = emailVal.message;
    if (!passVal.isValid) validationErrors.password = passVal.message;
    if (!orgVal.isValid) validationErrors.organizationId = orgVal.message;

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setLoading(true);
    try {
      const result = await apiClient.post('/v1/users', {
        name: nameVal.sanitizedValue,
        email: emailVal.sanitizedValue,
        password: password,
        organizationId: orgVal.sanitizedValue,
        role: role,
      });

      setSuccess(`User ${result.name} (${result.email}) successfully registered as ${result.role}!`);
      setName('');
      setEmail('');
      setPassword('');
    } catch (err) {
      setErrors({ general: err.message });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Page Header */}
      <div className="border-b border-medium-border dark:border-medium-border-dark pb-4">
        <h1 className="font-serif text-2xl sm:text-3xl font-bold tracking-tight text-medium-text dark:text-medium-text-dark flex items-center gap-2.5">
          <Users className="w-6 h-6 text-medium-green" />
          User Management
        </h1>
        <p className="text-xs sm:text-sm text-medium-muted dark:text-medium-muted-dark mt-1">
          Register new team members and assign operational roles within your organization.
        </p>
      </div>

      {/* Global Feedback Messages */}
      {success && (
        <div className="p-4 rounded-md bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 flex items-start gap-3 text-emerald-800 dark:text-emerald-300 text-sm">
          <CheckCircle2 className="w-5 h-5 flex-shrink-0 mt-0.5" />
          <span>{success}</span>
        </div>
      )}

      {errors.general && (
        <div className="p-4 rounded-md bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-800 flex items-start gap-3 text-red-800 dark:text-red-300 text-sm">
          <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
          <span>{errors.general}</span>
        </div>
      )}

      {/* User Creation Form Card */}
      <div className="p-6 rounded-lg bg-white dark:bg-medium-card-dark border border-medium-border dark:border-medium-border-dark shadow-xs space-y-6">
        <div className="flex items-center gap-2 text-sm font-semibold text-medium-text dark:text-medium-text-dark border-b border-medium-border dark:border-medium-border-dark pb-3">
          <UserPlus className="w-4 h-4 text-medium-green" />
          <span>Create New User</span>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Full Name"
              id="uName"
              placeholder="e.g. John Doe"
              value={name}
              onChange={(e) => setName(e.target.value)}
              error={errors.name}
              required
              maxLength={100}
            />

            <Input
              label="Corporate Email"
              id="uEmail"
              type="email"
              placeholder="john@company.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={errors.email}
              required
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Initial Password"
              id="uPassword"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              error={errors.password}
              required
            />

            <Input
              label="Organization ID"
              id="uOrgId"
              type="number"
              value={orgId}
              onChange={(e) => setOrgId(e.target.value)}
              error={errors.organizationId}
              required
            />
          </div>

          <Select
            label="Role / Access Profile"
            id="uRole"
            value={role}
            onChange={(e) => setRole(e.target.value)}
            options={ROLES}
            required
          />

          <div className="pt-4 flex justify-end">
            <button
              type="submit"
              disabled={loading}
              className="px-6 py-2.5 rounded-full bg-medium-green hover:bg-medium-green-hover text-white font-medium text-xs transition-all shadow-xs disabled:opacity-50 flex items-center gap-2"
            >
              {loading ? 'Creating...' : 'Create User'}
              {!loading && <UserPlus className="w-4 h-4" />}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
