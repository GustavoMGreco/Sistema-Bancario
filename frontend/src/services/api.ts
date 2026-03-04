import { Conta } from '../types/banco';

const BASE_URL = 'http://localhost:8080';

export const api = {
    async listarContas(): Promise<Conta[]> {
        const response = await fetch(`${BASE_URL}/contas`);
        if (!response.ok) throw new Error('Erro ao buscar contas');
        return response.json();
    },

    async transferir(origem: string, destino: string, valor: number): Promise<void> {
        const response = await fetch(`${BASE_URL}/transferencias`, {
            method: 'POST',
            headers: { 'Content-Type' : 'application.json' },
            body: JSON.stringify({ numeroOrigem: origem, numeroDestino: destino, valor })
        });
        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.erro || 'Erro na transferência');
        }
    }
}