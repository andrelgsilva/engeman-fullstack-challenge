import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import type { Role } from './types';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import ImoveisPage from './pages/imoveis/ImoveisPage';
import ImovelDetailPage from './pages/imoveis/ImovelDetailPage';

// Pages (vamos criar nas próximas tasks)


// import GestaoPage from './pages/gestao/GestaoPage';
// import FavoritosPage from './pages/favoritos/FavoritosPage';

function PrivateRoute({ children, roles }: { children: React.ReactNode; roles?: Role[] }) {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (roles && user && !roles.includes(user.role)) return <Navigate to="/" replace />;

  return <>{children}</>;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/" element={<ImoveisPage />} />
      <Route path="/imoveis/:id" element={<ImovelDetailPage />} />
      <Route
        path="/gestao"
        element={
          <PrivateRoute roles={['ADMIN', 'CORRETOR']}>
            <div>Gestão de Imóveis</div>
          </PrivateRoute>
        }
      />
      <Route
        path="/favoritos"
        element={
          <PrivateRoute roles={['CLIENTE']}>
            <div>Favoritos</div>
          </PrivateRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;