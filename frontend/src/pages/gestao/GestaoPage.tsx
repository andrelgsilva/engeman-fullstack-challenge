import { useState, useEffect, useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Plus, Pencil, Power, X } from 'lucide-react';
import Navbar from '../../components/Navbar';
import { imovelService } from '../../services/imovelService';
import { useAuth } from '../../contexts/AuthContext';
import type { Imovel, TipoImovel } from '../../types';

const TIPOS: { value: TipoImovel; label: string }[] = [
  { value: 'CASA', label: 'Casa' },
  { value: 'APARTAMENTO', label: 'Apartamento' },
  { value: 'TERRENO', label: 'Terreno' },
  { value: 'COMERCIAL', label: 'Comercial' },
];

const tipoLabel: Record<string, string> = {
  CASA: 'Casa',
  APARTAMENTO: 'Apartamento',
  TERRENO: 'Terreno',
  COMERCIAL: 'Comercial',
};

const imovelSchema = z.object({
  titulo: z.string().min(3, 'Título deve ter no mínimo 3 caracteres'),
  descricao: z.string().min(10, 'Descrição deve ter no mínimo 10 caracteres'),
  tipo: z.enum(['CASA', 'APARTAMENTO', 'TERRENO', 'COMERCIAL'], {
    error: 'Selecione um tipo',
  }),
  preco: z.number({ error: 'Preço é obrigatório' }).positive('Preço deve ser maior que zero'),
  quartos: z.number({ error: 'Quartos é obrigatório' }).int().min(0, 'Não pode ser negativo'),
  banheiros: z.number({ error: 'Banheiros é obrigatório' }).int().min(0, 'Não pode ser negativo'),
  areaM2: z.number({ error: 'Área é obrigatória' }).positive('Área deve ser maior que zero'),
  endereco: z.string().min(3, 'Endereço obrigatório'),
  cidade: z.string().min(2, 'Cidade obrigatória'),
  estado: z.string().min(2, 'Estado obrigatório'),
});

type ImovelForm = z.infer<typeof imovelSchema>;

export default function GestaoPage() {
  const { hasRole } = useAuth();

  const [imoveis, setImoveis] = useState<Imovel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [showForm, setShowForm] = useState(false);
  const [editando, setEditando] = useState<Imovel | null>(null);
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);

  const [statusLoadingId, setStatusLoadingId] = useState<number | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ImovelForm>({
    resolver: zodResolver(imovelSchema),
  });

  const buscar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await imovelService.listarGestao();
      setImoveis(result);
    } catch {
      setError('Erro ao carregar imóveis. Tente novamente.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    buscar();
  }, [buscar]);

  const abrirCriar = () => {
    setEditando(null);
    reset({
      titulo: '',
      descricao: '',
      tipo: 'CASA',
      preco: undefined,
      quartos: undefined,
      banheiros: undefined,
      areaM2: undefined,
      endereco: '',
      cidade: '',
      estado: '',
    });
    setFormError(null);
    setShowForm(true);
  };

  const abrirEditar = (imovel: Imovel) => {
    setEditando(imovel);
    reset({
      titulo: imovel.titulo,
      descricao: imovel.descricao,
      tipo: imovel.tipo,
      preco: imovel.preco,
      quartos: imovel.quartos,
      banheiros: imovel.banheiros,
      areaM2: imovel.areaM2,
      endereco: imovel.endereco,
      cidade: imovel.cidade,
      estado: imovel.estado,
    });
    setFormError(null);
    setShowForm(true);
  };

  const fecharForm = () => {
    setShowForm(false);
    setEditando(null);
  };

  const onSubmit = async (data: ImovelForm) => {
    setFormLoading(true);
    setFormError(null);
    try {
      if (editando) {
        await imovelService.atualizar(editando.id, data);
      } else {
        await imovelService.criar(data);
      }
      fecharForm();
      await buscar();
    } catch (err: any) {
      setFormError(err.response?.data?.message || 'Erro ao salvar imóvel. Tente novamente.');
    } finally {
      setFormLoading(false);
    }
  };

  const toggleStatus = async (imovel: Imovel) => {
    setStatusLoadingId(imovel.id);
    try {
      await imovelService.atualizarStatus(imovel.id, !imovel.ativo);
      setImoveis((prev) =>
        prev.map((i) => (i.id === imovel.id ? { ...i, ativo: !i.ativo } : i))
      );
    } catch {
      setError('Erro ao atualizar status do imóvel. Tente novamente.');
    } finally {
      setStatusLoadingId(null);
    }
  };

  const inputClass = (hasError: boolean) =>
    `w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 ${
      hasError ? 'border-red-400 bg-red-50' : 'border-gray-300'
    }`;

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-6xl mx-auto px-4 py-8">

        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Gestão de imóveis</h1>
            <p className="text-sm text-gray-500 mt-1">
              {hasRole('ADMIN') ? 'Todos os imóveis cadastrados' : 'Imóveis que você cadastrou'}
            </p>
          </div>
          <button
            onClick={abrirCriar}
            className="cursor-pointer flex items-center gap-2 bg-primary-600 hover:bg-primary-700 text-white text-sm px-4 py-2 rounded-lg transition-colors"
          >
            <Plus className="w-4 h-4" />
            Novo imóvel
          </button>
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
        {!loading && !error && imoveis.length === 0 && (
          <div className="text-center py-20">
            <p className="text-4xl mb-4">🏚️</p>
            <p className="text-gray-500 font-medium">Nenhum imóvel encontrado</p>
            <p className="text-gray-400 text-sm mt-1">Clique em "Novo imóvel" para cadastrar o primeiro</p>
          </div>
        )}

        {/* Tabela */}
        {!loading && !error && imoveis.length > 0 && (
          <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-200 text-gray-500 text-xs uppercase">
                <tr>
                  <th className="text-left px-4 py-3 font-medium">Imóvel</th>
                  <th className="text-left px-4 py-3 font-medium">Tipo</th>
                  <th className="text-left px-4 py-3 font-medium">Preço</th>
                  <th className="text-left px-4 py-3 font-medium">Quartos</th>
                  <th className="text-left px-4 py-3 font-medium">Status</th>
                  {hasRole('ADMIN') && <th className="text-left px-4 py-3 font-medium">Corretor</th>}
                  <th className="text-right px-4 py-3 font-medium">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {imoveis.map((imovel) => (
                  <tr key={imovel.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 font-medium text-gray-900">{imovel.titulo}</td>
                    <td className="px-4 py-3 text-gray-600">{tipoLabel[imovel.tipo] || imovel.tipo}</td>
                    <td className="px-4 py-3 text-gray-600">
                      {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(imovel.preco)}
                    </td>
                    <td className="px-4 py-3 text-gray-600">{imovel.quartos}</td>
                    <td className="px-4 py-3">
                      <span
                        className={`text-xs font-medium px-2 py-0.5 rounded-full ${
                          imovel.ativo ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
                        }`}
                      >
                        {imovel.ativo ? 'Ativo' : 'Inativo'}
                      </span>
                    </td>
                    {hasRole('ADMIN') && (
                      <td className="px-4 py-3 text-gray-600">{imovel.corretorNome}</td>
                    )}
                    <td className="px-4 py-3">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => abrirEditar(imovel)}
                          className="flex items-center gap-1 text-xs cursor-pointer border border-gray-300 rounded-lg px-2.5 py-1.5 hover:bg-gray-50 transition-colors"
                        >
                          <Pencil className="w-3.5 h-3.5" />
                          Editar
                        </button>
                        <button
                          onClick={() => toggleStatus(imovel)}
                          disabled={statusLoadingId === imovel.id}
                          className={`flex items-center gap-1 cursor-pointer text-xs rounded-lg px-2.5 py-1.5 border transition-colors disabled:opacity-50 ${
                            imovel.ativo
                              ? 'border-red-200 text-red-600 hover:bg-red-50'
                              : 'border-green-200 text-green-600 hover:bg-green-50'
                          }`}
                        >
                          <Power className="w-3.5 h-3.5" />
                          {imovel.ativo ? 'Desativar' : 'Ativar'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de criar/editar */}
      {showForm && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl shadow-lg w-full max-w-lg max-h-[90vh] overflow-y-auto p-6">

            <div className="flex items-center justify-between mb-6">
              <h2 className="text-lg font-bold text-gray-900">
                {editando ? 'Editar imóvel' : 'Novo imóvel'}
              </h2>
              <button onClick={fecharForm} className="text-gray-400 hover:text-gray-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            {formError && (
              <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg p-3 mb-4 text-sm">
                {formError}
              </div>
            )}

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Título</label>
                <input {...register('titulo')} className={inputClass(!!errors.titulo)} placeholder="Ex: Casa com 3 quartos no Boa Viagem" />
                {errors.titulo && <p className="text-red-500 text-xs mt-1">{errors.titulo.message}</p>}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Descrição</label>
                <textarea {...register('descricao')} rows={3} className={inputClass(!!errors.descricao)} placeholder="Detalhes do imóvel..." />
                {errors.descricao && <p className="text-red-500 text-xs mt-1">{errors.descricao.message}</p>}
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
                  <select {...register('tipo')} className={inputClass(!!errors.tipo)}>
                    {TIPOS.map((t) => <option key={t.value} value={t.value}>{t.label}</option>)}
                  </select>
                  {errors.tipo && <p className="text-red-500 text-xs mt-1">{errors.tipo.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Preço (R$)</label>
                  <input
                    {...register('preco', { valueAsNumber: true })}
                    type="number"
                    step="0.01"
                    className={inputClass(!!errors.preco)}
                    placeholder="450000"
                  />
                  {errors.preco && <p className="text-red-500 text-xs mt-1">{errors.preco.message}</p>}
                </div>
              </div>

              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Quartos</label>
                  <input {...register('quartos', { valueAsNumber: true })} type="number" className={inputClass(!!errors.quartos)} placeholder="3" />
                  {errors.quartos && <p className="text-red-500 text-xs mt-1">{errors.quartos.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Banheiros</label>
                  <input {...register('banheiros', { valueAsNumber: true })} type="number" className={inputClass(!!errors.banheiros)} placeholder="2" />
                  {errors.banheiros && <p className="text-red-500 text-xs mt-1">{errors.banheiros.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Área (m²)</label>
                  <input {...register('areaM2', { valueAsNumber: true })} type="number" step="0.01" className={inputClass(!!errors.areaM2)} placeholder="120" />
                  {errors.areaM2 && <p className="text-red-500 text-xs mt-1">{errors.areaM2.message}</p>}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Endereço</label>
                <input {...register('endereco')} className={inputClass(!!errors.endereco)} placeholder="Rua, número, bairro" />
                {errors.endereco && <p className="text-red-500 text-xs mt-1">{errors.endereco.message}</p>}
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Cidade</label>
                  <input {...register('cidade')} className={inputClass(!!errors.cidade)} placeholder="Recife" />
                  {errors.cidade && <p className="text-red-500 text-xs mt-1">{errors.cidade.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
                  <input {...register('estado')} className={inputClass(!!errors.estado)} placeholder="PE" />
                  {errors.estado && <p className="text-red-500 text-xs mt-1">{errors.estado.message}</p>}
                </div>
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={fecharForm}
                  className="cursor-pointer flex-1 border border-gray-300 hover:bg-gray-50 text-gray-600 text-sm py-2.5 rounded-lg transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={formLoading}
                  className="cursor-pointer flex-1 bg-primary-600 hover:bg-primary-700 disabled:bg-primary-500 text-white font-medium text-sm py-2.5 rounded-lg transition-colors flex items-center justify-center gap-2"
                >
                  {formLoading ? (
                    <>
                      <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                      Salvando...
                    </>
                  ) : (
                    editando ? 'Salvar alterações' : 'Cadastrar imóvel'
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}