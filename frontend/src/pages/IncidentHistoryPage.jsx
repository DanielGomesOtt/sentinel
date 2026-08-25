import React, { useState, useEffect, useCallback } from 'react';
import { apiClient } from '../services/apiClient';
import { StatusBadge } from '../components/common/Badge';
import { Pagination } from '../components/common/Pagination';
import { Input, Select } from '../components/common/Input';
import { validateId, sanitizeQueryParams } from '../utils/security';
import {
  History,
  Download,
  Search,
  Filter,
  RefreshCw,
  ArrowRight,
  User,
  Clock,
  Calendar,
} from 'lucide-react';

const STATUSES = [
  { value: 'OPEN', label: 'OPEN' },
  { value: 'UNDER_REVIEW', label: 'UNDER_REVIEW' },
  { value: 'IN_CORRECTION', label: 'IN_CORRECTION' },
  { value: 'RESOLVED', label: 'RESOLVED' },
  { value: 'CLOSED', label: 'CLOSED' },
];

export function IncidentHistoryPage({ defaultIncidentId }) {
  const [incidentId, setIncidentId] = useState(defaultIncidentId ? String(defaultIncidentId) : '');
  const [histories, setHistories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [pdfDownloading, setPdfDownloading] = useState(false);

  // Pagination
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Filters
  const [newStatus, setNewStatus] = useState('');
  const [previousStatus, setPreviousStatus] = useState('');
  const [action, setAction] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [userId, setUserId] = useState('');

  // Fetch Incident History
  const fetchHistories = useCallback(async () => {
    const idVal = validateId(incidentId, 'ID do Incidente');
    if (!idVal.isValid) {
      setHistories([]);
      setTotalPages(0);
      setTotalElements(0);
      setError('Por favor, informe um ID de incidente válido para consultar o histórico.');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const rawParams = {
        page,
        size,
        incidentId: idVal.sanitizedValue,
        newStatus,
        previousStatus,
        action,
        from: fromDate ? new Date(fromDate).toISOString() : undefined,
        to: toDate ? new Date(toDate).toISOString() : undefined,
        userId: userId ? Number(userId) : undefined,
      };

      const cleanParams = sanitizeQueryParams(rawParams);
      const data = await apiClient.get('/v1/incidentHistory', cleanParams);

      if (data) {
        setHistories(data.histories || []);
        setTotalPages(data.totalPages || 0);
        setTotalElements(data.totalElements || 0);
      }
    } catch (err) {
      setError(err.message || 'Erro ao carregar histórico.');
    } finally {
      setLoading(false);
    }
  }, [incidentId, page, size, newStatus, previousStatus, action, fromDate, toDate, userId]);

  useEffect(() => {
    if (incidentId) {
      fetchHistories();
    }
  }, [incidentId, page, size]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchHistories();
  };

  const handleExportPdf = async () => {
    const idVal = validateId(incidentId, 'ID do Incidente');
    if (!idVal.isValid) {
      alert('Informe um ID de incidente válido para exportar o relatório.');
      return;
    }

    setPdfDownloading(true);
    try {
      const rawParams = {
        page: 0,
        size: 100,
        incidentId: idVal.sanitizedValue,
        newStatus,
        previousStatus,
        action,
        from: fromDate ? new Date(fromDate).toISOString() : undefined,
        to: toDate ? new Date(toDate).toISOString() : undefined,
        userId: userId ? Number(userId) : undefined,
      };
      const cleanParams = sanitizeQueryParams(rawParams);
      await apiClient.downloadPdf(
        '/v1/incidentHistory/pdf',
        cleanParams,
        `historico_incidente_${incidentId}_${Date.now()}.pdf`
      );
    } catch (err) {
      alert(`Falha ao gerar PDF: ${err.message}`);
    } finally {
      setPdfDownloading(false);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return 'N/A';
    try {
      const d = new Date(dateStr);
      return d.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
    } catch (e) {
      return dateStr;
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-medium-border dark:border-medium-border-dark pb-4">
        <div>
          <h1 className="font-serif text-2xl sm:text-3xl font-bold tracking-tight text-medium-text dark:text-medium-text-dark flex items-center gap-2.5">
            <History className="w-6 h-6 text-medium-green" />
            Histórico de Alterações
          </h1>
          <p className="text-xs sm:text-sm text-medium-muted dark:text-medium-muted-dark mt-1">
            Audit trail e linha do tempo de mudanças registradas em cada incidente.
          </p>
        </div>

        <button
          onClick={handleExportPdf}
          disabled={pdfDownloading || loading || !incidentId}
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
            label="ID do Incidente *"
            id="hIncidentId"
            type="number"
            placeholder="Ex: 10"
            value={incidentId}
            onChange={(e) => setIncidentId(e.target.value)}
            icon={Search}
            required
          />

          <Select
            label="Novo Status"
            id="hNewStatus"
            value={newStatus}
            onChange={(e) => setNewStatus(e.target.value)}
            options={STATUSES}
            placeholder="Todos os Status"
          />

          <Select
            label="Status Anterior"
            id="hPrevStatus"
            value={previousStatus}
            onChange={(e) => setPreviousStatus(e.target.value)}
            options={STATUSES}
            placeholder="Todos os Status"
          />

          <Input
            label="Ação (Ex: UPDATE)"
            id="hAction"
            placeholder="Ação realizada..."
            value={action}
            onChange={(e) => setAction(e.target.value)}
          />

          <Input
            label="Data Inicial (De)"
            id="hFrom"
            type="date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
          />

          <Input
            label="Data Final (Até)"
            id="hTo"
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
          />
        </div>

        <div className="flex justify-end gap-2 pt-2 border-t border-medium-border dark:border-medium-border-dark">
          <button
            type="button"
            onClick={() => {
              setNewStatus('');
              setPreviousStatus('');
              setAction('');
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
            Buscar Histórico
          </button>
        </div>
      </form>

      {/* Error Banner */}
      {error && (
        <div className="p-4 rounded-md bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 text-amber-800 dark:text-amber-300 text-xs">
          {error}
        </div>
      )}

      {/* Timeline View */}
      <div className="border border-medium-border dark:border-medium-border-dark rounded-lg overflow-hidden bg-white dark:bg-medium-card-dark shadow-xs p-6">
        {loading ? (
          <div className="py-16 text-center text-medium-muted">
            <RefreshCw className="w-6 h-6 animate-spin mx-auto mb-2 text-medium-green" />
            <p className="text-xs">Carregando histórico do incidente...</p>
          </div>
        ) : histories.length === 0 ? (
          <div className="py-12 text-center text-medium-muted">
            <History className="w-8 h-8 mx-auto mb-2 opacity-40" />
            <p className="text-sm font-medium">Nenhum registro de histórico encontrado.</p>
            <p className="text-xs mt-1">Informe um ID de incidente válido no campo de busca.</p>
          </div>
        ) : (
          <div className="relative border-l-2 border-medium-border dark:border-medium-border-dark ml-4 space-y-6">
            {histories.map((item) => (
              <div key={item.id} className="relative pl-6 group">
                {/* Timeline node icon */}
                <div className="absolute -left-[9px] top-1 w-4 h-4 rounded-full bg-medium-green border-2 border-white dark:border-medium-card-dark" />

                <div className="p-4 rounded-lg bg-gray-50 dark:bg-medium-surface-dark border border-medium-border dark:border-medium-border-dark space-y-2">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                    <div className="flex items-center gap-2">
                      <span className="font-semibold text-xs text-medium-text dark:text-medium-text-dark uppercase tracking-wide">
                        {item.action || 'ALTERAÇÃO DE STATUS'}
                      </span>
                      <span className="font-mono text-[11px] text-medium-muted dark:text-medium-muted-dark">
                        (Registro #{item.id})
                      </span>
                    </div>

                    <div className="flex items-center gap-1.5 text-[11px] text-medium-muted dark:text-medium-muted-dark">
                      <Clock className="w-3.5 h-3.5" />
                      <span>{formatDate(item.createdAt)}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-3 py-1">
                    {item.previousStatus && (
                      <>
                        <StatusBadge status={item.previousStatus} />
                        <ArrowRight className="w-4 h-4 text-medium-muted" />
                      </>
                    )}
                    <StatusBadge status={item.newStatus} />
                  </div>

                  <div className="text-[11px] text-medium-muted dark:text-medium-muted-dark flex items-center gap-1.5 pt-1 border-t border-medium-border/50 dark:border-medium-border-dark/50">
                    <User className="w-3.5 h-3.5 text-medium-muted" />
                    <span>
                      Executado por ID: <strong className="text-medium-text dark:text-medium-text-dark font-medium">#{item.performedBy || item.performedBySystemIntegration || 'Sistema'}</strong>
                    </span>
                  </div>
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
    </div>
  );
}
