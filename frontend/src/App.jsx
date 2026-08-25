import React, { useState } from 'react';
import { ThemeProvider } from './context/ThemeContext';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Header } from './components/common/Header';
import { AuthPage } from './pages/AuthPage';
import { IncidentsPage } from './pages/IncidentsPage';
import { IncidentHistoryPage } from './pages/IncidentHistoryPage';
import { IncidentLogsPage } from './pages/IncidentLogsPage';
import { AdminUsersPage } from './pages/AdminUsersPage';
import { AdminIntegrationsPage } from './pages/AdminIntegrationsPage';

function MainContent() {
  const { isAuthenticated, isAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState('incidents');
  const [selectedIncidentId, setSelectedIncidentId] = useState(null);

  if (!isAuthenticated) {
    return <AuthPage />;
  }

  const handleNavigateToHistory = (incidentId) => {
    setSelectedIncidentId(incidentId);
    setActiveTab('history');
  };

  const handleNavigateToLogs = (incidentId) => {
    setSelectedIncidentId(incidentId);
    setActiveTab('logs');
  };

  return (
    <div className="min-h-screen flex flex-col bg-medium-bg dark:bg-medium-bg-dark transition-colors">
      <Header activeTab={activeTab} setActiveTab={setActiveTab} />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {activeTab === 'incidents' && (
          <IncidentsPage
            onViewHistory={handleNavigateToHistory}
            onViewLogs={handleNavigateToLogs}
          />
        )}

        {activeTab === 'history' && (
          <IncidentHistoryPage defaultIncidentId={selectedIncidentId} />
        )}

        {activeTab === 'logs' && (
          <IncidentLogsPage defaultIncidentId={selectedIncidentId} />
        )}

        {activeTab === 'users' && (
          isAdmin ? <AdminUsersPage /> : (
            <div className="p-6 text-center text-red-600 dark:text-red-400 font-medium">
              Access denied. Only administrators can manage users.
            </div>
          )
        )}

        {activeTab === 'integrations' && (
          isAdmin ? <AdminIntegrationsPage /> : (
            <div className="p-6 text-center text-red-600 dark:text-red-400 font-medium">
              Access denied. Only administrators can manage system integrations.
            </div>
          )
        )}
      </main>

      <footer className="border-t border-medium-border dark:border-medium-border-dark py-6 text-center text-xs text-medium-muted dark:text-medium-muted-dark">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2">
          <span>Sentinel Incident Management System &copy; {new Date().getFullYear()}</span>
          <span>Minimalist Design • React JS + Tailwind CSS</span>
        </div>
      </footer>
    </div>
  );
}

export function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <MainContent />
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
