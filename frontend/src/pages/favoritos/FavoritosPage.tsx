import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { Heart, MapPin, Bed } from 'lucide-react';
import Navbar from '../../components/Navbar';
import { favoritoService } from '../../services/favoritoService';
import type { Favorito } from '../../types';

const tipoLabel: Record<string, string> = {
  CASA: 'Casa',
  APARTAMENTO: 'Apartamento',
  TERRENO: 'Terreno',
  COMERCIAL: 'Comercial',
};

export default function FavoritosPage() {
  const [favoritos, setFavoritos] = useState<Favorito[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [removendoId, setRemovendoId] = useState<number | null>(null);

  const buscar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await favoritoService.listar();
      setFavoritos(result);
    } catch {
      setError('Erro ao carregar favoritos. Tente novamente.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    buscar();
  }, [buscar]);

  const remover = async (imovelId: number) => {
    setRemovendoId(imovelId);
    try {
      await favoritoService.remover(imovelId);
      setFavoritos((prev) => prev.filter((f) => f.imovelId !== imovelId));
    } catch {
      setError('Erro ao remover favorito. Tente novamente.');
    } finally {
      setRemovendoId(null);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-7xl mx-auto px-4 py-8">

        {/* Header */}
        <div className="mb-6">
          <h1 className="cursor-default     text-2xl font-bold text-gray-900">Meus favoritos</h1>
          {favoritos.length > 0 && (
            <p className="text-sm text-gray-500 mt-1">
              {favoritos.length} {favoritos.length === 1 ? 'imóvel favoritado' : 'imóveis favoritados'}
            </p>
          )}
        </div>

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
            <button onClick={buscar} className="mt-3 text-sm underline">
              Tentar novamente
            </button>
          </div>
        )}

        {/* Vazio */}
        {!loading && !error && favoritos.length === 0 && (
          <div className="text-center py-20">
            <p className="text-gray-500 font-medium">Nenhum imóvel favoritado</p>
            <p className="text-gray-400 text-sm mt-1">
              Explore os imóveis disponíveis e favorite os que mais gostar
            </p>
            <Link
              to="/"
              className="inline-block mt-4 bg-primary-600 hover:bg-primary-700 text-white text-sm px-4 py-2 rounded-lg transition-colors"
            >
              Ver imóveis
            </Link>
          </div>
        )}

        {/* Grid de favoritos */}
        {!loading && !error && favoritos.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {favoritos.map((favorito) => (
              <div
                key={favorito.favoritoId}
                className="bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-md transition-shadow"
              >
                <Link to={`/imoveis/${favorito.imovelId}`} className="block">
                  <div className="bg-gray-100 h-48 flex items-center justify-center">
                    <span className="text-4xl">🏠</span>
                  </div>
                </Link>

                <div className="p-4">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-medium bg-primary-100 text-primary-700 px-2 py-0.5 rounded-full">
                      {tipoLabel[favorito.tipo] || favorito.tipo}
                    </span>
                    <button
                      onClick={() => remover(favorito.imovelId)}
                      disabled={removendoId === favorito.imovelId}
                      className="text-gray-400 hover:text-orange-500 transition-colors disabled:opacity-50"
                      title="Remover dos favoritos"
                    >
                      <Heart className="w-5 h-5 fill-orange-500 text-orange-500" />
                    </button>
                  </div>

                  <Link to={`/imoveis/${favorito.imovelId}`}>
                    <h3 className="font-semibold text-gray-900 hover:text-primary-600 transition-colors line-clamp-1">
                      {favorito.titulo}
                    </h3>
                  </Link>

                  <div className="flex items-center gap-1 text-gray-500 text-xs mt-1 mb-3">
                    <MapPin className="w-3 h-3" />
                    {favorito.cidade}, {favorito.estado}
                  </div>

                  <div className="flex items-center gap-3 text-xs text-gray-500 mb-3">
                    <span className="flex items-center gap-1">
                      <Bed className="w-3 h-3" />
                      {favorito.quartos} quartos
                    </span>
                  </div>

                  <p className="text-primary-600 font-bold text-lg">
                    {new Intl.NumberFormat('pt-BR', {
                      style: 'currency',
                      currency: 'BRL',
                    }).format(favorito.preco)}
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}