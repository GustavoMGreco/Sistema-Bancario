export interface Cliente {
    nome: string;
    cpf: string;
}

export interface Conta {
    tipo?: string;      // ? = opcional, para diferenciar conta corrente e poupança
    numero: string;
    agencia: string;
    nomeCliente: string;
    saldo: number;
    limiteChequeEspecial?: number; 
    taxaRendimento?: number; 
}

