import React, { useState } from 'react';
import { apiClient } from '../services/apiClient';
import { Input } from '../components/common/Input';
import { validateText } from '../utils/security';
import { KeyRound, Shield, CheckCircle2, AlertCircle, Copy, Check, Terminal, Cpu } from 'lucide-react';

export function AdminIntegrationsPage() {
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [createdIntegration, setCreatedIntegration] = useState(null);
  const [copiedField, setCopiedField] = useState(null);

  // System token testing state
  const [testClientId, setTestClientId] = useState('');
  const [testClientSecret, setTestClientSecret] = useState('');
  const [testToken, setTestToken] = useState('');
  const [testLoading, setTestLoading] = useState(false);
  const [testError, setTestError] = useState(null);

  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setCreatedIntegration(null);

    const nameVal = validateText(name, 'Integration Name', 3, 100);
    if (!nameVal.isValid) {
      setError(nameVal.message);
      return;
    }

    setLoading(true);
    try {
      const data = await apiClient.post('/v1/systemIntegration', {
        name: nameVal.sanitizedValue,
      });

      setCreatedIntegration(data);
      setName('');
      if (data.clientId) {
        setTestClientId(data.clientId);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleTestTokenSubmit = async (e) => {
    e.preventDefault();
    setTestError(null);
    setTestToken('');

    if (!testClientId || !testClientSecret) {
      setTestError('Please provide both Client ID and Client Secret to issue a token.');
      return;
    }

    setTestLoading(true);
    try {
      const res = await apiClient.post('/v1/auth/token', {
        clientId: testClientId.trim(),
        clientSecret: testClientSecret.trim(),
      });

      setTestToken(res.token);
    } catch (err) {
      setTestError(err.message);
    } finally {
      setTestLoading(false);
    }
  };

  const copyToClipboard = (text, fieldName) => {
    navigator.clipboard.writeText(text);
    setCopiedField(fieldName);
    setTimeout(() => setCopiedField(null), 2000);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      {/* Page Header */}
      <div className="border-b border-medium-border dark:border-medium-border-dark pb-4">
        <h1 className="font-serif text-2xl sm:text-3xl font-bold tracking-tight text-medium-text dark:text-medium-text-dark flex items-center gap-2.5">
          <KeyRound className="w-6 h-6 text-medium-green" />
          System Integrations (M2M)
        </h1>
        <p className="text-xs sm:text-sm text-medium-muted dark:text-medium-muted-dark mt-1">
          Generate credentials for external systems to authenticate and register automated incidents (`ROLE_SYSTEM`).
        </p>
      </div>

      {/* 1. Register Integration Form */}
      <div className="p-6 rounded-lg bg-white dark:bg-medium-card-dark border border-medium-border dark:border-medium-border-dark shadow-xs space-y-6">
        <div className="flex items-center gap-2 text-sm font-semibold text-medium-text dark:text-medium-text-dark border-b border-medium-border dark:border-medium-border-dark pb-3">
          <Cpu className="w-4 h-4 text-medium-green" />
          <span>Register New System Integration</span>
        </div>

        {error && (
          <div className="p-4 rounded-md bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-800 flex items-start gap-3 text-red-800 dark:text-red-300 text-xs">
            <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleCreateSubmit} className="space-y-4">
          <Input
            label="External System Name"
            id="sysName"
            placeholder="e.g. Datadog Monitoring / Prometheus Alertmanager"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            maxLength={100}
            helpText="Unique identifier for the external monitoring or service integration."
          />

          <div className="flex justify-end">
            <button
              type="submit"
              disabled={loading}
              className="px-5 py-2.5 rounded-full bg-medium-green hover:bg-medium-green-hover text-white font-medium text-xs transition-all shadow-xs disabled:opacity-50 flex items-center gap-2"
            >
              {loading ? 'Generating Credentials...' : 'Generate Integration Credentials'}
            </button>
          </div>
        </form>

        {/* Display Generated Credentials Result */}
        {createdIntegration && (
          <div className="mt-6 p-4 rounded-lg bg-emerald-50/70 dark:bg-emerald-950/30 border border-emerald-200 dark:border-emerald-800 space-y-4 animate-fadeIn">
            <div className="flex items-center gap-2 text-xs font-semibold text-emerald-800 dark:text-emerald-300">
              <CheckCircle2 className="w-4 h-4" />
              <span>Credentials Created for: {createdIntegration.name}</span>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs font-mono">
              <div className="p-3 bg-white dark:bg-medium-surface-dark rounded border border-emerald-200 dark:border-emerald-900">
                <div className="text-[10px] text-medium-muted uppercase font-sans mb-1 font-semibold">Client ID</div>
                <div className="flex items-center justify-between gap-2 text-medium-text dark:text-medium-text-dark break-all">
                  <span>{createdIntegration.clientId}</span>
                  <button
                    onClick={() => copyToClipboard(createdIntegration.clientId, 'clientId')}
                    className="p-1 text-medium-muted hover:text-medium-text"
                    title="Copy Client ID"
                  >
                    {copiedField === 'clientId' ? <Check className="w-3.5 h-3.5 text-emerald-500" /> : <Copy className="w-3.5 h-3.5" />}
                  </button>
                </div>
              </div>

              <div className="p-3 bg-white dark:bg-medium-surface-dark rounded border border-emerald-200 dark:border-emerald-900">
                <div className="text-[10px] text-medium-muted uppercase font-sans mb-1 font-semibold">Client Secret Hash</div>
                <div className="flex items-center justify-between gap-2 text-medium-text dark:text-medium-text-dark break-all">
                  <span className="truncate">{createdIntegration.clientSecretHash || '••••••••••••••••'}</span>
                  {createdIntegration.clientSecretHash && (
                    <button
                      onClick={() => copyToClipboard(createdIntegration.clientSecretHash, 'secret')}
                      className="p-1 text-medium-muted hover:text-medium-text"
                      title="Copy Hash"
                    >
                      {copiedField === 'secret' ? <Check className="w-3.5 h-3.5 text-emerald-500" /> : <Copy className="w-3.5 h-3.5" />}
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* 2. System Token Tester Tool */}
      <div className="p-6 rounded-lg bg-white dark:bg-medium-card-dark border border-medium-border dark:border-medium-border-dark shadow-xs space-y-4">
        <div className="flex items-center gap-2 text-sm font-semibold text-medium-text dark:text-medium-text-dark border-b border-medium-border dark:border-medium-border-dark pb-3">
          <Terminal className="w-4 h-4 text-medium-green" />
          <span>Test System JWT Generator (`POST /v1/auth/token`)</span>
        </div>

        {testError && (
          <div className="p-3 rounded bg-red-50 dark:bg-red-950/40 border border-red-200 text-red-700 dark:text-red-300 text-xs">
            {testError}
          </div>
        )}

        <form onSubmit={handleTestTokenSubmit} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Client ID"
              id="tClientId"
              placeholder="integration-client-id"
              value={testClientId}
              onChange={(e) => setTestClientId(e.target.value)}
              required
            />

            <Input
              label="Client Secret"
              id="tClientSecret"
              type="password"
              placeholder="integration-client-secret"
              value={testClientSecret}
              onChange={(e) => setTestClientSecret(e.target.value)}
              required
            />
          </div>

          <div className="flex justify-end">
            <button
              type="submit"
              disabled={testLoading}
              className="px-4 py-2 rounded-full border border-medium-border dark:border-medium-border-dark text-xs font-medium text-medium-text dark:text-medium-text-dark hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
            >
              {testLoading ? 'Generating Token...' : 'Generate Test Token'}
            </button>
          </div>
        </form>

        {testToken && (
          <div className="p-4 rounded bg-gray-900 text-gray-100 space-y-2 text-xs font-mono">
            <div className="flex items-center justify-between text-gray-400 font-sans">
              <span>Issued JWT Token:</span>
              <button
                onClick={() => copyToClipboard(testToken, 'token')}
                className="hover:text-white flex items-center gap-1"
              >
                {copiedField === 'token' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                <span>{copiedField === 'token' ? 'Copied' : 'Copy JWT'}</span>
              </button>
            </div>
            <p className="break-all text-emerald-400">{testToken}</p>
          </div>
        )}
      </div>
    </div>
  );
}
