import api from './api';
import type { Imovel, ImovelFiltros, PageResponse } from '../types';

export const imovelService = {
  listar: async (filtros?: ImovelFiltros): Promise<PageResponse<Imovel>> => {
    const response = await api.get<PageResponse<Imovel>>('/api/imoveis/buscar', {
      params: filtros,
    });
    return response.data;
  },

  buscarPorId: async (id: number): Promise<Imovel> => {
    const response = await api.get<Imovel>(`/api/imoveis/${id}`);
    return response.data;
  },

  criar: async (data: Partial<Imovel>): Promise<Imovel> => {
    const response = await api.post<Imovel>('/api/imoveis', data);
    return response.data;
  },

  atualizar: async (id: number, data: Partial<Imovel>): Promise<Imovel> => {
    const response = await api.put<Imovel>(`/api/imoveis/${id}`, data);
    return response.data;
  },

  atualizarStatus: async (id: number, ativo: boolean): Promise<Imovel> => {
    const response = await api.patch<Imovel>(`/api/imoveis/${id}/status`, { ativo });
    return response.data;
  },
};