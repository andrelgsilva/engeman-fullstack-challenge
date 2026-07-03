import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor de request — adiciona o token JWT em todas as requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor de response — trata erros globalmente
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isAuthEndpoint = error.config?.url?.includes('/auth/login') || error.config?.url?.includes('/auth/register');

    // 401 em rota autenticada = sessão expirada/token inválido → desloga.
    // 401 em /auth/login ou /auth/register = credenciais erradas → deixa a tela tratar normalmente.
    if (error.response?.status === 401 && !isAuthEndpoint) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }

    // 403: usuário autenticado mas sem permissão para a ação — não desloga,
    // só garante uma mensagem amigável quando o backend não manda uma
    if (error.response?.status === 403 && !error.response?.data?.message) {
      error.response.data = {
        ...error.response.data,
        message: 'Você não tem permissão para realizar esta ação.',
      };
    }

    return Promise.reject(error);
  }
);

export default api;