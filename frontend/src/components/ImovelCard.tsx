import { Link } from 'react-router-dom';
import { MapPin, Bed, Bath, Maximize } from 'lucide-react';
import type { Imovel } from '../types';

interface Props {
  imovel: Imovel;
}

const tipoLabel: Record<string, string> = {
  CASA: 'Casa',
  APARTAMENTO: 'Apartamento',
  TERRENO: 'Terreno',
  COMERCIAL: 'Comercial',
};

export default function ImovelCard({ imovel }: Props) {
  return (
    <Link to={`/imoveis/${imovel.id}`} className="block group">
      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-md transition-shadow">
        
        {/* Imagem placeholder */}
        <div className="bg-gray-100 h-48 flex items-center justify-center">
          <span className="text-4xl">🏠</span>
        </div>

        <div className="p-4">
          {/* Tipo + status */}
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium bg-primary-100 text-primary-700 px-2 py-0.5 rounded-full">
              {tipoLabel[imovel.tipo] || imovel.tipo}
            </span>
            {!imovel.ativo && (
              <span className="text-xs font-medium bg-gray-100 text-gray-500 px-2 py-0.5 rounded-full">
                Inativo
              </span>
            )}
          </div>

          {/* Título */}
          <h3 className="font-semibold text-gray-900 group-hover:text-primary-600 transition-colors line-clamp-1">
            {imovel.titulo}
          </h3>

          {/* Localização */}
          <div className="flex items-center gap-1 text-gray-500 text-xs mt-1 mb-3">
            <MapPin className="w-3 h-3" />
            {imovel.cidade}, {imovel.estado}
          </div>

          {/* Detalhes */}
          <div className="flex items-center gap-3 text-xs text-gray-500 mb-3">
            <span className="flex items-center gap-1">
              <Bed className="w-3 h-3" />
              {imovel.quartos} quartos
            </span>
            {imovel.banheiros && (
              <span className="flex items-center gap-1">
                <Bath className="w-3 h-3" />
                {imovel.banheiros} banheiros
              </span>
            )}
            {imovel.areaM2 && (
              <span className="flex items-center gap-1">
                <Maximize className="w-3 h-3" />
                {imovel.areaM2}m²
              </span>
            )}
          </div>

          {/* Preço */}
          <p className="text-primary-600 font-bold text-lg">
            {new Intl.NumberFormat('pt-BR', {
              style: 'currency',
              currency: 'BRL',
            }).format(imovel.preco)}
          </p>
        </div>
      </div>
    </Link>
  );
}