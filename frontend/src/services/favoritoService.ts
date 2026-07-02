import api from './api';
import type { Favorito } from '../types';

export const favoritoService = {
  listar: async (): Promise<Favorito[]> => {
    const response = await api.get<Favorito[]>('/api/favoritos');
    return response.data;
  },

  adicionar: async (imovelId: number): Promise<Favorito> => {
    const response = await api.post<Favorito>(`/api/favoritos/${imovelId}`);
    return response.data;
  },

  remover: async (imovelId: number): Promise<void> => {
    await api.delete(`/api/favoritos/${imovelId}`);
  },
};