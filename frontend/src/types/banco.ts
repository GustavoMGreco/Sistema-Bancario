export interface Cliente {
    nome: string;
    cpf: string;
}

export interface Conta {
    tipo?: string;
    numero: string;
    agencia: string;
    nomeCliente: string;
    saldo: number;
    limiteChequeEspecial?: number;  // opcional
    taxaRendimento?: number;  // opcional
}

