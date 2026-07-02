import { Link, useNavigate } from 'react-router-dom';
import { Home, Heart, Settings, LogOut, LogIn } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2 font-bold text-primary-600 text-lg">
          <Home className="w-5 h-5" />
          Engeman Imóveis
        </Link>

        {/* Nav links */}
        <div className="flex items-center gap-4">
          {isAuthenticated && user?.role === 'CLIENTE' && (
            <Link
              to="/favoritos"
              className="flex items-center gap-1.5 text-sm text-gray-600 hover:text-primary-600 transition-colors"
            >
              <Heart className="w-4 h-4" />
              Favoritos
            </Link>
          )}

          {isAuthenticated && (user?.role === 'ADMIN' || user?.role === 'CORRETOR') && (
            <Link
              to="/gestao"
              className="flex items-center gap-1.5 text-sm text-gray-600 hover:text-primary-600 transition-colors"
            >
              <Settings className="w-4 h-4" />
              Gestão
            </Link>
          )}

          {isAuthenticated ? (
            <div className="flex items-center gap-3 cursor-default">
              <span className="text-sm text-gray-500">
                Olá, <span className="font-medium text-gray-700">{user?.name}</span>
              </span>
              <button
                onClick={handleLogout}
                className="flex items-center gap-1.5 cursor-pointer text-sm text-gray-600 hover:text-red-500 transition-colors"
              >
                <LogOut className="w-4 h-4" />
                Sair
              </button>
            </div>
          ) : (
            <Link
              to="/login"
              className="flex items-center gap-1.5 text-sm bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg transition-colors"
            >
              <LogIn className="w-4 h-4" />
              Entrar
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
}