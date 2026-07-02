import { useState, useEffect, useCallback } from 'react';
import { Search, SlidersHorizontal, ChevronLeft, ChevronRight } from 'lucide-react';
import Navbar from '../../components/Navbar'
import ImovelCard from '../../components/ImovelCard';
import { imovelService } from '../../services/imovelService';
import type { Imovel, ImovelFiltros, PageResponse, TipoImovel } from '../../types';

const TIPOS: { value: TipoImovel | ''; label: string }[] = [
  { value: '', label: 'Todos os tipos' },
  { value: 'CASA', label: 'Casa' },
  { value: 'APARTAMENTO', label: 'Apartamento' },
  { value: 'TERRENO', label: 'Terreno' },
  { value: 'COMERCIAL', label: 'Comercial' },
];

const ORDENACAO = [
  { value: 'createdAt', label: 'Mais recentes' },
  { value: 'preco', label: 'Menor preço' },
  { value: 'preco-desc', label: 'Maior preço' },
  { value: 'quartos', label: 'Mais quartos' },
];

export default function ImoveisPage() {
  const [data, setData] = useState<PageResponse<Imovel> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showFilters, setShowFilters] = useState(false);

  const [filtros, setFiltros] = useState<ImovelFiltros>({
    page: 0,
    size: 9,
    sortBy: 'createdAt',
    sortDir: 'desc',
  });

  const [titulo, setTitulo] = useState('');
  const [tipo, setTipo] = useState<TipoImovel | ''>('');
  const [precoMin, setPrecoMin] = useState('');
  const [precoMax, setPrecoMax] = useState('');
  const [quartosMin, setQuartosMin] = useState('');
  const [ordenacao, setOrdenacao] = useState('createdAt');

  const buscar = useCallback(async (f: ImovelFiltros) => {
    setLoading(true);
    setError(null);
    try {
      const result = await imovelService.listar(f);
      setData(result);
    } catch {
      setError('Erro ao carregar imóveis. Tente novamente.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    buscar(filtros);
  }, [filtros, buscar]);

  const aplicarFiltros = () => {
    const [sortBy, sortDir] = ordenacao.includes('-desc')
      ? [ordenacao.replace('-desc', ''), 'desc']
      : [ordenacao, 'asc'];

    setFiltros({
      page: 0,
      size: 9,
      titulo: titulo || undefined,
      tipo: tipo || undefined,
      precoMin: precoMin ? Number(precoMin) : undefined,
      precoMax: precoMax ? Number(precoMax) : undefined,
      quartosMin: quartosMin ? Number(quartosMin) : undefined,
      sortBy,
      sortDir: sortDir as 'asc' | 'desc',
    });
  };

  const limparFiltros = () => {
    setTitulo('');
    setTipo('');
    setPrecoMin('');
    setPrecoMax('');
    setQuartosMin('');
    setOrdenacao('createdAt');
    setFiltros({ page: 0, size: 9, sortBy: 'createdAt', sortDir: 'desc' });
  };

  const inputClass = 'w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500';

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-7xl mx-auto px-4 py-8">

        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl cursor-default font-bold text-gray-900">Imóveis disponíveis</h1>
            {data && (
              <p className="cursor-default text-sm text-gray-500 mt-1">
                {data.totalElements} {data.totalElements === 1 ? 'imóvel encontrado' : 'imóveis encontrados'}
              </p>
            )}
          </div>
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="flex items-center gap-2 cursor-pointer border border-gray-300 rounded-lg px-4 py-2 text-sm hover:bg-gray-100 transition-colors"
          >
            <SlidersHorizontal className="w-4 h-4" />
            Filtros
          </button>
        </div>

        {/* Busca por nome */}
        <div className="relative mb-4">
          <button
            type="button"
            onClick={aplicarFiltros}
            className="absolute left-0 top-0 h-full w-12 flex items-center justify-center cursor-pointer text-gray-400 hover:text-primary-600 transition-colors z-10"
          >
            <Search className="w-4 h-4" />
          </button>

          <input
            type="text"
            placeholder="Buscar imóvel por nome..."
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && aplicarFiltros()}
            className="w-full pl-14 pr-4 py-2.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>

        {/* Painel de filtros */}
        {showFilters && (
          <div className="bg-white border border-gray-200 rounded-xl p-4 mb-6">
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3">
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Tipo</label>
                <select value={tipo} onChange={(e) => setTipo(e.target.value as TipoImovel | '')} className={inputClass}>
                  {TIPOS.map((t) => <option key={t.value} value={t.value}>{t.label}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Preço mínimo</label>
                <input type="number" placeholder="R$ 0" value={precoMin} onChange={(e) => setPrecoMin(e.target.value)} className={inputClass} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Preço máximo</label>
                <input type="number" placeholder="R$ 999.999" value={precoMax} onChange={(e) => setPrecoMax(e.target.value)} className={inputClass} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Quartos mínimos</label>
                <input type="number" placeholder="0" min="0" value={quartosMin} onChange={(e) => setQuartosMin(e.target.value)} className={inputClass} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Ordenar por</label>
                <select value={ordenacao} onChange={(e) => setOrdenacao(e.target.value)} className={inputClass}>
                  {ORDENACAO.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              </div>
            </div>
            <div className="flex gap-2 mt-4">
              <button onClick={aplicarFiltros} className="bg-primary-600 hover:bg-primary-700 text-white text-sm px-4 py-2 rounded-lg transition-colors">
                Aplicar filtros
              </button>
              <button onClick={limparFiltros} className="border border-gray-300 hover:bg-gray-50 text-gray-600 text-sm px-4 py-2 rounded-lg transition-colors">
                Limpar
              </button>
            </div>
          </div>
        )}

        {/* Loading */}
        {loading && (
          <div className="flex items-center justify-center py-20">
            <div className="w-8 h-8 border-4 border-primary-600 border-t-transparent rounded-full animate-spin" />
          </div>
        )}

        {/* Erro */}
        {error && !loading && (
          <div className="bg-red-50 border border-red-200 text-red-700 rounded-xl p-6 text-center">
            <p className="font-medium">{error}</p>
            <button onClick={() => buscar(filtros)} className="mt-3 text-sm underline">
              Tentar novamente
            </button>
          </div>
        )}

        {/* Vazio */}
        {!loading && !error && data?.content.length === 0 && (
          <div className="text-center py-20">
            <p className="text-4xl mb-4">🏚️</p>
            <p className="text-gray-500 font-medium">Nenhum imóvel encontrado</p>
            <p className="text-gray-400 text-sm mt-1">Tente ajustar os filtros de busca</p>
          </div>
        )}

        {/* Grid de imóveis */}
        {!loading && !error && data && data.content.length > 0 && (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {data.content.map((imovel) => (
                <ImovelCard key={imovel.id} imovel={imovel} />
              ))}
            </div>

            {/* Paginação */}
            {data.totalPages > 1 && (
              <div className="flex items-center justify-center gap-2 mt-8">
                <button
                  onClick={() => setFiltros((f) => ({ ...f, page: (f.page ?? 0) - 1 }))}
                  disabled={data.first}
                  className="flex items-center gap-1 px-3 py-2 border border-gray-300 rounded-lg text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
                >
                  <ChevronLeft className="w-4 h-4" />
                  Anterior
                </button>

                <span className="text-sm text-gray-600">
                  Página {data.page + 1} de {data.totalPages}
                </span>

                <button
                  onClick={() => setFiltros((f) => ({ ...f, page: (f.page ?? 0) + 1 }))}
                  disabled={data.last}
                  className="flex items-center gap-1 px-3 py-2 border border-gray-300 rounded-lg text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
                >
                  Próxima
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}