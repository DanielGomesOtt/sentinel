import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../services/apiClient';
import { SeverityBadge, StatusBadge } from '../components/common/Badge';
import { Pagination } from '../components/common/Pagination';
import { Modal } from '../components/common/Modal';
import { Input, Select } from '../components/common/Input';
import { validateText, validateEnum, sanitizeQueryParams } from '../utils/security';
import {
  Plus,
  Download,
  Filter,
  RefreshCw,
  AlertTriangle,
  Clock,
  Edit2,
  Eye,
  Search,
  CheckCircle,
  XCircle,
  FileText,
} from 'lucide-react';

const SEVERITIES = [
  { value: 'CRITICAL', label: 'CRITICAL (Crítico)' },
  { value: 'HIGH', label: 'HIGH (Alto)' },
  { value: 'MEDIUM', label: 'MEDIUM (Médio)' },
  { value: 'LOW', label: 'LOW (Baixo)' },
];

const STATUSES = [
  { value: 'OPEN', label: 'OPEN (Aberto)' },
  { value: 'UNDER_REVIEW', label: 'UNDER_REVIEW (Em Análise)' },
  { value: 'IN_CORRECTION', label: 'IN_CORRECTION (Em Correção)' },
  { value: 'RESOLVED', label: 'RESOLVED (Resolvido)' },
  { value: 'CLOSED', label: 'CLOSED (Fechado)' },
];

export function IncidentsPage({ onViewHistory, onViewLogs }) {
  const { isTech } = useAuth();

  // Data & loading states
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [pdfDownloading, setPdfDownloading] = useState(false);

  // Pagination state
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Filter state
  const [filterTitle, setFilterTitle] = useState('');
  const [filterDescription, setFilterDescription] = useState('');
  const [filterSeverity, setFilterSeverity] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterService, setFilterService] = useState('');
  const [filterSlaViolate, setFilterSlaViolate] = useState(false);

  // Modal states
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [selectedIncident, setSelectedIncident] = useState(null);

  // Form states (Create / Edit)
  const [formTitle, setFormTitle] = useState('');
  const [formDescription, setFormDescription] = useState('');
  const [formSeverity, setFormSeverity] = useState('HIGH');
  const [formService, setFormService] = useState('');
  const [formStatus, setFormStatus] = useState('OPEN');
  const [formErrors, setFormErrors] = useState({});

  // Fetch incidents list from backend
  const fetchIncidents = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const rawParams = {
        page,
        size,
        title: filterTitle,
        description: filterDescription,
        severity: filterSeverity,
        status: filterStatus,
        serviceName: filterService,
        slaViolate: filterSlaViolate ? true : undefined,
      };

      const cleanParams = sanitizeQueryParams(rawParams);
      const data = await apiClient.get('/v1/incidents', cleanParams);

      if (data) {
        setIncidents(data.incidents || []);
        setTotalPages(data.totalPages || 0);
        setTotalElements(data.totalElements || 0);
      }
    } catch (err) {
      setError(err.message || 'Erro ao carregar lista de incidentes.');
    } finally {
      setLoading(false);
    }
  }, [page, size, filterTitle, filterDescription, filterSeverity, filterStatus, filterService, filterSlaViolate]);

  useEffect(() => {
    fetchIncidents();
  }, [fetchIncidents]);

  // Handle PDF Download
  const handleExportPdf = async () => {
    setPdfDownloading(true);
    try {
      const rawParams = {
        page: 0,
        size: 100, // Export current view/filtered items
        title: filterTitle,
        description: filterDescription,
        severity: filterSeverity,
        status: filterStatus,
        serviceName: filterService,
        slaViolate: filterSlaViolate ? true : undefined,
      };
      const cleanParams = sanitizeQueryParams(rawParams);
      await apiClient.downloadPdf('/v1/incidents/pdf', cleanParams, `sentinel_incidents_${Date.now()}.pdf`);
    } catch (err) {
      alert(`Falha ao exportar PDF: ${err.message}`);
    } finally {
      setPdfDownloading(false);
    }
  };

  // Open Create Modal
  const openCreateModal = () => {
    setFormTitle('');
    setFormDescription('');
    setFormSeverity('HIGH');
    setFormService('');
    setFormErrors({});
    setIsCreateOpen(true);
  };

  // Handle Create Incident Submit
  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    setFormErrors({});

    const titleVal = validateText(formTitle, 'Título', 3, 200);
    const descVal = validateText(formDescription, 'Descrição', 5, 2000);
    const sevVal = validateEnum(formSeverity, ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW'], 'Severidade');
    const serviceVal = validateText(formService, 'Serviço', 2, 100);

    const errors = {};
    if (!titleVal.isValid) errors.title = titleVal.message;
    if (!descVal.isValid) errors.description = descVal.message;
    if (!sevVal.isValid) errors.severity = sevVal.message;
    if (!serviceVal.isValid) errors.serviceName = serviceVal.message;

    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }

    try {
      await apiClient.post('/v1/incidents', {
        title: titleVal.sanitizedValue,
        description: descVal.sanitizedValue,
        severity: sevVal.sanitizedValue,
        serviceName: serviceVal.sanitizedValue,
      });

      setIsCreateOpen(false);
      fetchIncidents();
    } catch (err) {
      setFormErrors({ general: err.message });
    }
  };

  // Open Edit Modal
  const openEditModal = (inc) => {
    setSelectedIncident(inc);
    setFormTitle(inc.title || '');
    setFormDescription(inc.description || '');
    setFormSeverity(inc.severity || 'MEDIUM');
    setFormService(inc.serviceName || '');
    setFormStatus(inc.status || 'OPEN');
    setFormErrors({});
    setIsEditOpen(true);
  };

  // Handle Edit Incident Submit
  const handleEditSubmit = async (e) => {
    e.preventDefault();
    setFormErrors({});

    if (!selectedIncident?.id) return;

    const titleVal = validateText(formTitle, 'Título', 3, 200);
    const descVal = validateText(formDescription, 'Descrição', 5, 2000);
    const sevVal = validateEnum(formSeverity, ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW'], 'Severidade');
    const statusVal = validateEnum(formStatus, ['OPEN', 'UNDER_REVIEW', 'IN_CORRECTION', 'RESOLVED', 'CLOSED'], 'Status');
    const serviceVal = validateText(formService, 'Serviço', 2, 100);

    const errors = {};
    if (!titleVal.isValid) errors.title = titleVal.message;
    if (!descVal.isValid) errors.description = descVal.message;
    if (!sevVal.isValid) errors.severity = sevVal.message;
    if (!statusVal.isValid) errors.status = statusVal.message;
    if (!serviceVal.isValid) errors.serviceName = serviceVal.message;

    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }

    try {
      await apiClient.put('/v1/incidents', {
        incidentId: selectedIncident.id,
        title: titleVal.sanitizedValue,
        description: descVal.sanitizedValue,
        severity: sevVal.sanitizedValue,
        serviceName: serviceVal.sanitizedValue,
        incidentStatus: statusVal.sanitizedValue,
      });

      setIsEditOpen(false);
      fetchIncidents();
    } catch (err) {
      setFormErrors({ general: err.message });
    }
  };

  // Open Detail Modal
  const openDetailModal = (inc) => {
    setSelectedIncident(inc);
    setIsDetailOpen(true);
  };

  // Format date utility
  const formatDate = (dateStr) => {
    if (!dateStr) return 'N/A';
    try {
      const d = new Date(dateStr);
      return d.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
    } catch (e) {
      return dateStr;
    }
  };

  // Check if SLA deadline is exceeded
  const isSlaViolated = (deadlineStr, status) => {
    if (!deadlineStr || status === 'RESOLVED' || status === 'CLOSED') return false;
    try {
      return new Date(deadlineStr) < new Date();
    } catch (e) {
      return false;
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-medium-border dark:border-medium-border-dark pb-4">
        <div>
          <h1 className="font-serif text-2xl sm:text-3xl font-bold tracking-tight text-medium-text dark:text-medium-text-dark">
            Gestão de Incidentes
          </h1>
          <p className="text-xs sm:text-sm text-medium-muted dark:text-medium-muted-dark mt-1">
            Monitore, crie e atualize o ciclo de vida dos incidentes operacionais.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={handleExportPdf}
            disabled={pdfDownloading || loading}
            className="inline-flex items-center gap-2 px-3 py-2 rounded-md border border-medium-border dark:border-medium-border-dark text-xs font-medium text-medium-text dark:text-medium-text-dark hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors disabled:opacity-50"
          >
            <Download className="w-4 h-4 text-medium-muted" />
            <span>{pdfDownloading ? 'Exportando...' : 'Exportar PDF'}</span>
          </button>

          <button
            onClick={openCreateModal}
            className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-medium-green hover:bg-medium-green-hover text-white text-xs font-medium transition-all shadow-xs"
          >
            <Plus className="w-4 h-4" />
            <span>Novo Incidente</span>
          </button>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="p-4 rounded-lg bg-gray-50 dark:bg-medium-card-dark border border-medium-border dark:border-medium-border-dark space-y-4">
        <div className="flex items-center justify-between text-xs font-semibold text-medium-text dark:text-medium-text-dark">
          <div className="flex items-center gap-2">
            <Filter className="w-4 h-4 text-medium-muted" />
            <span>Filtros Avançados</span>
          </div>
          <button
            onClick={() => {
              setFilterTitle('');
              setFilterDescription('');
              setFilterSeverity('');
              setFilterStatus('');
              setFilterService('');
              setFilterSlaViolate(false);
              setPage(0);
            }}
            className="text-medium-muted hover:text-medium-text dark:hover:text-medium-text-dark font-normal transition-colors"
          >
            Limpar Filtros
          </button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3">
          <Input
            id="fTitle"
            placeholder="Buscar por título..."
            value={filterTitle}
            onChange={(e) => { setFilterTitle(e.target.value); setPage(0); }}
            icon={Search}
          />

          <Input
            id="fService"
            placeholder="Nome do serviço..."
            value={filterService}
            onChange={(e) => { setFilterService(e.target.value); setPage(0); }}
          />

          <Select
            id="fSeverity"
            value={filterSeverity}
            onChange={(e) => { setFilterSeverity(e.target.value); setPage(0); }}
            options={SEVERITIES}
            placeholder="Todas Severidades"
          />

          <Select
            id="fStatus"
            value={filterStatus}
            onChange={(e) => { setFilterStatus(e.target.value); setPage(0); }}
            options={STATUSES}
            placeholder="Todos os Status"
          />

          <div className="flex items-center justify-start h-full pt-1">
            <label className="inline-flex items-center gap-2 cursor-pointer text-xs font-medium text-medium-text dark:text-medium-text-dark select-none">
              <input
                type="checkbox"
                checked={filterSlaViolate}
                onChange={(e) => { setFilterSlaViolate(e.target.checked); setPage(0); }}
                className="rounded border-medium-border text-medium-green focus:ring-medium-green"
              />
              <span className="flex items-center gap-1 text-red-600 dark:text-red-400 font-semibold">
                <AlertTriangle className="w-3.5 h-3.5" />
                SLA Violado
              </span>
            </label>
          </div>
        </div>
      </div>

      {/* Error Feedback */}
      {error && (
        <div className="p-4 rounded-md bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-300 text-xs flex items-center justify-between">
          <span>{error}</span>
          <button onClick={fetchIncidents} className="underline flex items-center gap-1">
            <RefreshCw className="w-3 h-3" /> Tentar novamente
          </button>
        </div>
      )}

      {/* Data Table */}
      <div className="border border-medium-border dark:border-medium-border-dark rounded-lg overflow-hidden bg-white dark:bg-medium-card-dark shadow-xs">
        {loading ? (
          <div className="py-16 text-center text-medium-muted dark:text-medium-muted-dark">
            <RefreshCw className="w-6 h-6 animate-spin mx-auto mb-2 text-medium-green" />
            <p className="text-xs">Carregando incidentes...</p>
          </div>
        ) : incidents.length === 0 ? (
          <div className="py-16 text-center text-medium-muted dark:text-medium-muted-dark">
            <FileText className="w-8 h-8 mx-auto mb-2 opacity-40" />
            <p className="text-sm font-medium">Nenhum incidente encontrado.</p>
            <p className="text-xs mt-1">Tente ajustar os filtros ou criar um novo incidente.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-medium-border dark:border-medium-border-dark bg-gray-50 dark:bg-medium-surface-dark text-[11px] font-semibold tracking-wider text-medium-muted dark:text-medium-muted-dark uppercase">
                  <th className="py-3 px-4">ID</th>
                  <th className="py-3 px-4">Título / Serviço</th>
                  <th className="py-3 px-4">Severidade</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">SLA Deadline</th>
                  <th className="py-3 px-4 text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-medium-border dark:divide-medium-border-dark text-xs">
                {incidents.map((inc) => {
                  const violated = isSlaViolated(inc.slaDeadline, inc.status);
                  return (
                    <tr
                      key={inc.id}
                      className="hover:bg-gray-50/80 dark:hover:bg-medium-surface-dark/50 transition-colors"
                    >
                      <td className="py-3.5 px-4 font-mono text-medium-muted dark:text-medium-muted-dark font-medium">
                        #{inc.id}
                      </td>

                      <td className="py-3.5 px-4 max-w-xs">
                        <div className="font-semibold text-medium-text dark:text-medium-text-dark truncate">
                          {inc.title}
                        </div>
                        <div className="text-[11px] text-medium-muted dark:text-medium-muted-dark flex items-center gap-1 mt-0.5">
                          <span className="font-mono bg-gray-100 dark:bg-gray-800 px-1.5 py-0.5 rounded">
                            {inc.serviceName}
                          </span>
                        </div>
                      </td>

                      <td className="py-3.5 px-4">
                        <SeverityBadge severity={inc.severity} />
                      </td>

                      <td className="py-3.5 px-4">
                        <StatusBadge status={inc.status} />
                      </td>

                      <td className="py-3.5 px-4">
                        <div className="flex items-center gap-1.5">
                          <Clock className={`w-3.5 h-3.5 ${violated ? 'text-red-500 animate-pulse' : 'text-medium-muted'}`} />
                          <span className={violated ? 'text-red-600 dark:text-red-400 font-bold' : 'text-medium-text dark:text-medium-text-dark'}>
                            {formatDate(inc.slaDeadline)}
                          </span>
                        </div>
                        {violated && (
                          <span className="text-[10px] text-red-600 dark:text-red-400 font-semibold block">
                            Violado!
                          </span>
                        )}
                      </td>

                      <td className="py-3.5 px-4 text-right space-x-1">
                        <button
                          onClick={() => openDetailModal(inc)}
                          className="p-1.5 rounded-md hover:bg-gray-100 dark:hover:bg-gray-800 text-medium-muted hover:text-medium-text dark:hover:text-medium-text-dark transition-colors"
                          title="Ver Detalhes"
                        >
                          <Eye className="w-4 h-4" />
                        </button>

                        {isTech && (
                          <button
                            onClick={() => openEditModal(inc)}
                            className="p-1.5 rounded-md hover:bg-gray-100 dark:hover:bg-gray-800 text-medium-green hover:text-medium-green-hover transition-colors"
                            title="Editar Incidente (Tech/Admin)"
                          >
                            <Edit2 className="w-4 h-4" />
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination Controls */}
        <Pagination
          currentPage={page}
          totalPages={totalPages}
          totalElements={totalElements}
          pageSize={size}
          onPageChange={(newPage) => setPage(newPage)}
          onSizeChange={(newSize) => { setSize(newSize); setPage(0); }}
        />
      </div>

      {/* CREATE INCIDENT MODAL */}
      <Modal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        title="Criar Novo Incidente"
      >
        <form onSubmit={handleCreateSubmit} className="space-y-4">
          {formErrors.general && (
            <div className="p-3 rounded bg-red-50 dark:bg-red-950/40 border border-red-200 text-red-700 dark:text-red-300 text-xs">
              {formErrors.general}
            </div>
          )}

          <Input
            label="Título do Incidente"
            id="cTitle"
            placeholder="Ex: Queda na API de Pagamentos"
            value={formTitle}
            onChange={(e) => setFormTitle(e.target.value)}
            error={formErrors.title}
            required
            maxLength={200}
          />

          <Input
            label="Serviço Afetado"
            id="cService"
            placeholder="Ex: payment-gateway"
            value={formService}
            onChange={(e) => setFormService(e.target.value)}
            error={formErrors.serviceName}
            required
            maxLength={100}
          />

          <Select
            label="Severidade"
            id="cSeverity"
            value={formSeverity}
            onChange={(e) => setFormSeverity(e.target.value)}
            options={SEVERITIES}
            error={formErrors.severity}
            required
          />

          <Input
            label="Descrição Detalhada"
            id="cDesc"
            type="textarea"
            placeholder="Descreva a falha ou indisponibilidade identificada..."
            value={formDescription}
            onChange={(e) => setFormDescription(e.target.value)}
            error={formErrors.description}
            required
            maxLength={2000}
          />

          <div className="flex justify-end gap-3 pt-4 border-t border-medium-border dark:border-medium-border-dark">
            <button
              type="button"
              onClick={() => setIsCreateOpen(false)}
              className="px-4 py-2 rounded-full border border-medium-border dark:border-medium-border-dark text-xs font-medium text-medium-text dark:text-medium-text-dark hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-5 py-2 rounded-full bg-medium-green hover:bg-medium-green-hover text-white text-xs font-medium transition-colors"
            >
              Criar Incidente
            </button>
          </div>
        </form>
      </Modal>

      {/* EDIT INCIDENT MODAL */}
      <Modal
        isOpen={isEditOpen}
        onClose={() => setIsEditOpen(false)}
        title={`Atualizar Incidente #${selectedIncident?.id}`}
      >
        <form onSubmit={handleEditSubmit} className="space-y-4">
          {formErrors.general && (
            <div className="p-3 rounded bg-red-50 dark:bg-red-950/40 border border-red-200 text-red-700 dark:text-red-300 text-xs">
              {formErrors.general}
            </div>
          )}

          <Input
            label="Título do Incidente"
            id="eTitle"
            value={formTitle}
            onChange={(e) => setFormTitle(e.target.value)}
            error={formErrors.title}
            required
            maxLength={200}
          />

          <Input
            label="Serviço"
            id="eService"
            value={formService}
            onChange={(e) => setFormService(e.target.value)}
            error={formErrors.serviceName}
            required
            maxLength={100}
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Select
              label="Severidade"
              id="eSeverity"
              value={formSeverity}
              onChange={(e) => setFormSeverity(e.target.value)}
              options={SEVERITIES}
              error={formErrors.severity}
              required
            />

            <Select
              label="Status do Incidente"
              id="eStatus"
              value={formStatus}
              onChange={(e) => setFormStatus(e.target.value)}
              options={STATUSES}
              error={formErrors.status}
              required
            />
          </div>

          <Input
            label="Descrição Atualizada"
            id="eDesc"
            type="textarea"
            value={formDescription}
            onChange={(e) => setFormDescription(e.target.value)}
            error={formErrors.description}
            required
            maxLength={2000}
          />

          <div className="flex justify-end gap-3 pt-4 border-t border-medium-border dark:border-medium-border-dark">
            <button
              type="button"
              onClick={() => setIsEditOpen(false)}
              className="px-4 py-2 rounded-full border border-medium-border dark:border-medium-border-dark text-xs font-medium text-medium-text dark:text-medium-text-dark hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-5 py-2 rounded-full bg-medium-green hover:bg-medium-green-hover text-white text-xs font-medium transition-colors"
            >
              Salvar Alterações
            </button>
          </div>
        </form>
      </Modal>

      {/* INCIDENT DETAIL MODAL */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => setIsDetailOpen(false)}
        title={`Detalhes do Incidente #${selectedIncident?.id}`}
      >
        {selectedIncident && (
          <div className="space-y-4 text-xs">
            <div>
              <h4 className="font-serif text-lg font-bold text-medium-text dark:text-medium-text-dark">
                {selectedIncident.title}
              </h4>
              <div className="flex items-center gap-2 mt-2">
                <SeverityBadge severity={selectedIncident.severity} />
                <StatusBadge status={selectedIncident.status} />
                <span className="font-mono bg-gray-100 dark:bg-gray-800 px-2 py-0.5 rounded text-medium-muted">
                  {selectedIncident.serviceName}
                </span>
              </div>
            </div>

            <div className="p-3 rounded bg-gray-50 dark:bg-medium-surface-dark border border-medium-border dark:border-medium-border-dark whitespace-pre-wrap leading-relaxed text-medium-text dark:text-medium-text-dark">
              {selectedIncident.description}
            </div>

            <div className="grid grid-cols-2 gap-3 text-medium-muted dark:text-medium-muted-dark">
              <div>
                <span className="font-medium text-medium-text dark:text-medium-text-dark">Criado Por (ID):</span> #{selectedIncident.createdBy || 'Sistema'}
              </div>
              <div>
                <span className="font-medium text-medium-text dark:text-medium-text-dark">SLA Deadline:</span> {formatDate(selectedIncident.slaDeadline)}
              </div>
            </div>

            <div className="flex flex-wrap gap-2 pt-4 border-t border-medium-border dark:border-medium-border-dark">
              <button
                onClick={() => {
                  setIsDetailOpen(false);
                  if (onViewHistory) onViewHistory(selectedIncident.id);
                }}
                className="px-3 py-1.5 rounded border border-medium-border dark:border-medium-border-dark hover:bg-gray-100 dark:hover:bg-gray-800 text-medium-text dark:text-medium-text-dark transition-colors"
              >
                Ver Histórico de Alterações
              </button>

              <button
                onClick={() => {
                  setIsDetailOpen(false);
                  if (onViewLogs) onViewLogs(selectedIncident.id);
                }}
                className="px-3 py-1.5 rounded border border-medium-border dark:border-medium-border-dark hover:bg-gray-100 dark:hover:bg-gray-800 text-medium-text dark:text-medium-text-dark transition-colors"
              >
                Ver Logs do Incidente
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}
