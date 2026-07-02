import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, MapPin, Bed, Bath, Maximize, Heart, User } from 'lucide-react';
import Navbar from '../../components/Navbar';
import { imovelService } from '../../services/imovelService';
import { favoritoService } from '../../services/favoritoService';
import { useAuth } from '../../contexts/AuthContext';
import type { Imovel } from '../../types';

const tipoLabel: Record<string, string> = {
  CASA: 'Casa',
  APARTAMENTO: 'Apartamento',
  TERRENO: 'Terreno',
  COMERCIAL: 'Comercial',
};

export default function ImovelDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { hasRole } = useAuth();

  const [imovel, setImovel] = useState<Imovel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isFavorito, setIsFavorito] = useState(false);
  const [favoritoLoading, setFavoritoLoading] = useState(false);

  const buscar = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const result = await imovelService.buscarPorId(Number(id));
      setImovel(result);
    } catch {
      setError('Erro ao carregar imóvel. Tente novamente.');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    buscar();
  }, [buscar]);

  // Verifica se o imóvel já está nos favoritos do cliente (não bloqueia o carregamento principal)
  useEffect(() => {
    if (!id || !hasRole('CLIENTE')) return;

    favoritoService
      .listar()
      .then((favoritos) => {
        setIsFavorito(favoritos.some((f) => f.imovelId === Number(id)));
      })
      .catch(() => {
        // silencioso: não impede a visualização do imóvel
      });
  }, [id, hasRole]);

  const handleFavoritar = async () => {
    if (!imovel) return;
    setFavoritoLoading(true);
    try {
      if (isFavorito) {
        await favoritoService.remover(imovel.id);
        setIsFavorito(false);
      } else {
        await favoritoService.adicionar(imovel.id);
        setIsFavorito(true);
      }
    } catch {
      setError('Erro ao atualizar favorito. Tente novamente.');
    } finally {
      setFavoritoLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-5xl mx-auto px-4 py-8">

        {/* Voltar */}
        <button
          onClick={() => navigate('/')}
          className="flex items-center gap-1 cursor-pointer text-sm text-gray-500 hover:text-primary-600 transition-colors mb-6"
        >
          <ArrowLeft className="w-4 h-4" />
          Voltar para listagem
        </button>

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

        {/* Conteúdo */}
        {!loading && !error && imovel && (
          <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">

            {/* Imagem placeholder */}
            <div className="bg-gray-100 h-72 flex items-center justify-center">
              <span className="text-6xl">🏠</span>
            </div>

            <div className="p-6">
              {/* Tipo + status + favoritar */}
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-medium bg-primary-100 text-primary-700 px-2 py-0.5 rounded-full">
                    {tipoLabel[imovel.tipo] || imovel.tipo}
                  </span>
                  <span
                    className={`text-xs font-medium px-2 py-0.5 rounded-full ${
                      imovel.ativo
                        ? 'bg-green-100 text-green-700'
                        : 'bg-gray-100 text-gray-500'
                    }`}
                  >
                    {imovel.ativo ? 'Ativo' : 'Inativo'}
                  </span>
                </div>

                {hasRole('CLIENTE') && (
                  <button
                    onClick={handleFavoritar}
                    disabled={favoritoLoading}
                    className={`flex items-center gap-1 text-sm cursor-pointer border rounded-lg px-3 py-1.5 transition-colors disabled:opacity-50 ${
                      isFavorito
                        ? 'border-orange-300 bg-orange-50 text-orange-600 hover:bg-orange-100'
                        : 'border-gray-300 text-gray-600 hover:bg-gray-50'
                    }`}
                  >
                    <Heart
                      className={`w-4 h-4 ${isFavorito ? 'fill-orange-500 text-orange-500' : 'text-gray-500'}`}
                    />
                    {isFavorito ? 'Favoritado' : 'Favoritar'}
                  </button>
                )}
              </div>

              {/* Título */}
              <h1 className="text-2xl font-bold text-gray-900 mb-1">{imovel.titulo}</h1>

              {/* Localização */}
              <div className="flex items-center gap-1 text-gray-500 text-sm mb-4">
                <MapPin className="w-4 h-4" />
                {imovel.endereco}, {imovel.cidade} - {imovel.estado}
              </div>

              {/* Preço */}
              <p className="text-primary-600 font-bold text-3xl mb-6">
                {new Intl.NumberFormat('pt-BR', {
                  style: 'currency',
                  currency: 'BRL',
                }).format(imovel.preco)}
              </p>

              {/* Detalhes */}
              <div className="flex items-center gap-6 text-sm text-gray-600 border-y border-gray-100 py-4 mb-6">
                <span className="flex items-center gap-1.5">
                  <Bed className="w-4 h-4" />
                  {imovel.quartos} quartos
                </span>
                <span className="flex items-center gap-1.5">
                  <Bath className="w-4 h-4" />
                  {imovel.banheiros} banheiros
                </span>
                <span className="flex items-center gap-1.5">
                  <Maximize className="w-4 h-4" />
                  {imovel.areaM2}m²
                </span>
              </div>

              {/* Descrição */}
              <div className="mb-6">
                <h2 className="font-semibold text-gray-900 mb-2">Descrição</h2>
                <p className="text-gray-600 text-sm leading-relaxed whitespace-pre-line">
                  {imovel.descricao}
                </p>
              </div>

              {/* Corretor responsável */}
              <div className="flex items-center gap-2 text-sm text-gray-500 border-t border-gray-100 pt-4">
                <User className="w-4 h-4" />
                Anunciado por <span className="font-medium text-gray-700">{imovel.corretorNome}</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}