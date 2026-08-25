import React, { useState, useEffect, useCallback } from 'react';
import { apiClient } from '../services/apiClient';
import { LogLevelBadge } from '../components/common/Badge';
import { Pagination } from '../components/common/Pagination';
import { Modal } from '../components/common/Modal';
import { Input, Select } from '../components/common/Input';
import { sanitizeQueryParams } from '../utils/security';
import {
  Terminal,
  Download,
  Search,
  RefreshCw,
  Code2,
  Copy,
  Check,
  Calendar,
  AlertCircle,
  FileCode,
} from 'lucide-react';

const LOG_LEVELS = [
  { value: 'INFO', label: 'INFO' },
  { value: 'WARN', label: 'WARN' },
  { value: 'ERROR', label: 'ERROR' },
];

export function IncidentLogsPage({ defaultIncidentId }) {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [pdfDownloading, setPdfDownloading] = useState(false);

  // Pagination
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Filters
  const [incidentId, setIncidentId] = useState(defaultIncidentId ? String(defaultIncidentId) : '');
  const [level, setLevel] = useState('');
  const [message, setMessage] = useState('');
  const [serviceName, setServiceName] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [userId, setUserId] = useState('');

  // Stack trace modal
  const [activeStackTrace, setActiveStackTrace] = useState(null);
  const [copied, setCopied] = useState(false);

  const fetchLogs = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const rawParams = {
        page,
        size,
        incidentId: incidentId ? Number(incidentId) : undefined,
        incidentLogLevel: level || undefined,
        message: message || undefined,
        serviceName: serviceName || undefined,
        from: fromDate ? new Date(fromDate).toISOString() : undefined,
        to: toDate ? new Date(toDate).toISOString() : undefined,
        userId: userId ? Number(userId) : undefined,
      };

      const cleanParams = sanitizeQueryParams(rawParams);
      const data = await apiClient.get('/v1/incidentLog', cleanParams);

      if (data) {
        setLogs(data.logs || []);
        setTotalPages(data.totalPages || 0);
        setTotalElements(data.totalElements || 0);
      }
    } catch (err) {
      setError(err.message || 'Erro ao carregar logs de incidentes.');
    } finally {
      setLoading(false);
    }
  }, [page, size, incidentId, level, message, serviceName, fromDate, toDate, userId]);

  useEffect(() => {
    fetchLogs();
  }, [fetchLogs]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchLogs();
  };

  const handleExportPdf = async () => {
    setPdfDownloading(true);
    try {
      const rawParams = {
        page: 0,
        size: 100,
        incidentId: incidentId ? Number(incidentId) : undefined,
        incidentLogLevel: level || undefined,
        message: message || undefined,
        serviceName: serviceName || undefined,
        from: fromDate ? new Date(fromDate).toISOString() : undefined,
        to: toDate ? new Date(toDate).toISOString() : undefined,
        userId: userId ? Number(userId) : undefined,
      };
      const cleanParams = sanitizeQueryParams(rawParams);
      await apiClient.downloadPdf('/v1/incidentLog/pdf', cleanParams, `sentinel_logs_${Date.now()}.pdf`);
    } catch (err) {
      alert(`Falha ao gerar PDF de logs: ${err.message}`);
    } finally {
      setPdfDownloading(false);
    }
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-medium-border dark:border-medium-border-dark pb-4">
        <div>
          <h1 className="font-serif text-2xl sm:text-3xl font-bold tracking-tight text-medium-text dark:text-medium-text-dark flex items-center gap-2.5">
            <Terminal className="w-6 h-6 text-medium-green" />
            Logs de Incidentes
          </h1>
          <p className="text-xs sm:text-sm text-medium-muted dark:text-medium-muted-dark mt-1">
            Consulta técnica de registros de logs, exceções e diagnósticos do sistema.
          </p>
        </div>

        <button
          onClick={handleExportPdf}
          disabled={pdfDownloading || loading}
          className="inline-flex items-center gap-2 px-3 py-2 rounded-md border border-medium-border dark:border-medium-border-dark text-xs font-medium text-medium-text dark:text-medium-text-dark hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors disabled:opacity-50"
        >
          <Download className="w-4 h-4 text-medium-muted" />
          <span>{pdfDownloading ? 'Exportando...' : 'Exportar PDF'}</span>
        </button>
      </div>

      {/* Filter / Search Bar */}
      <form onSubmit={handleSearchSubmit} className="p-4 rounded-lg bg-gray-50 dark:bg-medium-card-dark border border-medium-border dark:border-medium-border-dark space-y-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
          <Input
            label="ID do Incidente"
            id="lIncidentId"
            type="number"
            placeholder="Ex: 1"
            value={incidentId}
            onChange={(e) => setIncidentId(e.target.value)}
          />

          <Select
            label="Nível de Log"
            id="lLevel"
            value={level}
            onChange={(e) => setLevel(e.target.value)}
            options={LOG_LEVELS}
            placeholder="Todos os Níveis"
          />

          <Input
            label="Serviço"
            id="lService"
            placeholder="Nome do serviço..."
            value={serviceName}
            onChange={(e) => setServiceName(e.target.value)}
          />

          <Input
            label="Mensagem do Log"
            id="lMessage"
            placeholder="Buscar no texto..."
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            icon={Search}
          />

          <Input
            label="Data Inicial (De)"
            id="lFrom"
            type="date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
          />

          <Input
            label="Data Final (Até)"
            id="lTo"
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
          />
        </div>

        <div className="flex justify-end gap-2 pt-2 border-t border-medium-border dark:border-medium-border-dark">
          <button
            type="button"
            onClick={() => {
              setIncidentId('');
              setLevel('');
              setMessage('');
              setServiceName('');
              setFromDate('');
              setToDate('');
              setUserId('');
            }}
            className="px-3 py-1.5 rounded-full border border-medium-border dark:border-medium-border-dark text-xs text-medium-muted hover:text-medium-text dark:hover:text-medium-text-dark"
          >
            Limpar Filtros
          </button>

          <button
            type="submit"
            className="px-4 py-1.5 rounded-full bg-medium-green hover:bg-medium-green-hover text-white text-xs font-medium transition-colors"
          >
            Filtrar Logs
          </button>
        </div>
      </form>

      {/* Error Feedback */}
      {error && (
        <div className="p-4 rounded-md bg-red-50 dark:bg-red-950/40 border border-red-200 text-red-700 dark:text-red-300 text-xs">
          {error}
        </div>
      )}

      {/* Logs Table / Cards */}
      <div className="border border-medium-border dark:border-medium-border-dark rounded-lg overflow-hidden bg-white dark:bg-medium-card-dark shadow-xs">
        {loading ? (
          <div className="py-16 text-center text-medium-muted">
            <RefreshCw className="w-6 h-6 animate-spin mx-auto mb-2 text-medium-green" />
            <p className="text-xs">Carregando logs...</p>
          </div>
        ) : logs.length === 0 ? (
          <div className="py-16 text-center text-medium-muted">
            <Terminal className="w-8 h-8 mx-auto mb-2 opacity-40" />
            <p className="text-sm font-medium">Nenhum log encontrado.</p>
          </div>
        ) : (
          <div className="divide-y divide-medium-border dark:divide-medium-border-dark">
            {logs.map((log, idx) => (
              <div key={idx} className="p-4 hover:bg-gray-50/70 dark:hover:bg-medium-surface-dark/40 transition-colors space-y-2">
                <div className="flex flex-wrap items-center justify-between gap-2 text-xs">
                  <div className="flex items-center gap-2">
                    <LogLevelBadge level={log.level} />
                    <span className="font-mono text-medium-muted font-medium">
                      Incidente #{log.incidentId}
                    </span>
                    <span className="font-mono bg-gray-100 dark:bg-gray-800 text-medium-text dark:text-medium-text-dark px-2 py-0.5 rounded text-[11px]">
                      {log.serviceName}
                    </span>
                  </div>

                  {log.stackTrace && (
                    <button
                      onClick={() => setActiveStackTrace(log.stackTrace)}
                      className="inline-flex items-center gap-1 text-xs text-medium-green hover:underline font-medium"
                    >
                      <Code2 className="w-3.5 h-3.5" />
                      Ver Stacktrace
                    </button>
                  )}
                </div>

                <div className="font-mono text-xs text-medium-text dark:text-medium-text-dark bg-gray-50 dark:bg-medium-surface-dark p-2.5 rounded border border-medium-border/60 dark:border-medium-border-dark/60 overflow-x-auto whitespace-pre-wrap">
                  {log.message}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Pagination */}
        <Pagination
          currentPage={page}
          totalPages={totalPages}
          totalElements={totalElements}
          pageSize={size}
          onPageChange={(newPage) => setPage(newPage)}
          onSizeChange={(newSize) => { setSize(newSize); setPage(0); }}
        />
      </div>

      {/* StackTrace Modal */}
      <Modal
        isOpen={!!activeStackTrace}
        onClose={() => setActiveStackTrace(null)}
        title="Stacktrace do Erro"
        maxWidth="max-w-3xl"
      >
        {activeStackTrace && (
          <div className="space-y-3">
            <div className="flex justify-end">
              <button
                onClick={() => copyToClipboard(activeStackTrace)}
                className="inline-flex items-center gap-1.5 px-3 py-1 rounded bg-gray-100 dark:bg-gray-800 text-xs font-medium text-medium-text dark:text-medium-text-dark hover:bg-gray-200 transition-colors"
              >
                {copied ? <Check className="w-3.5 h-3.5 text-emerald-500" /> : <Copy className="w-3.5 h-3.5" />}
                <span>{copied ? 'Copiado!' : 'Copiar Stacktrace'}</span>
              </button>
            </div>

            <pre className="p-4 rounded bg-gray-900 text-gray-100 font-mono text-xs overflow-x-auto max-h-[60vh] leading-relaxed">
              {activeStackTrace}
            </pre>
          </div>
        )}
      </Modal>
    </div>
  );
}
