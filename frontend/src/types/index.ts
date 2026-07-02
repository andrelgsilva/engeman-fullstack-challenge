export type Role = 'ADMIN' | 'CORRETOR' | 'CLIENTE';

export type TipoImovel = 'CASA' | 'APARTAMENTO' | 'TERRENO' | 'COMERCIAL';

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
  active: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  name: string;
  email: string;
  role: Role;
}

export interface Imovel {
  id: number;
  titulo: string;
  descricao: string;
  tipo: TipoImovel;
  preco: number;
  quartos: number;
  banheiros: number;
  areaM2: number;
  endereco: string;
  cidade: string;
  estado: string;
  ativo: boolean;
  corretorId: number;
  corretorNome: string;
  createdAt: string;
  updatedAt: string;
}

export interface ImovelFiltros {
  titulo?: string;
  tipo?: TipoImovel;
  precoMin?: number;
  precoMax?: number;
  quartosMin?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface Favorito {
  favoritoId: number;
  imovelId: number;
  titulo: string;
  tipo: TipoImovel;
  preco: number;
  quartos: number;
  cidade: string;
  estado: string;
  favoritadoEm: string;
}

export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp: string;
}