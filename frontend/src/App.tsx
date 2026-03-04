import { useEffect, useState } from 'react';
import { api } from './services/api';
import { Conta } from './types/banco';

function App() {
  const [contas, setContas] = useState<Conta[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [contaOrigem, setContaOrigem] = useState<string>('');
  const [contaDestino, setContaDestino] = useState<string>('');
  const [transferencia, setTransferencia] = useState<number>(0);

  async function btnTransferirClick() {
    try {    
      setErro(null)

      await api.transferir(contaOrigem, contaDestino, transferencia);
      const contasAtualizadas = await api.listarContas();
      setContas(contasAtualizadas)

    } catch (err: unknown) {
        setErro(err instanceof Error ? err.message : 'Erro desconhecido')
    } 
    finally {
      setContaOrigem('')
      setContaDestino('')
      setTransferencia(0)
    }
  }

  useEffect(() => {
    api.listarContas()
      .then(setContas)
      .catch(err => setErro(err.message));
  }, []);

  return (
    <>
      <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
        <h1>Banco Hexa - Painel Administrativo</h1>
        
        {erro && <p style={{ color: 'red' }}>{erro}</p>}

        <div style={{ display: 'grid', gap: '10px' }}>
          {contas.map(conta => (
            <div key={conta.numero} style={{ border: '1px solid #ccc', padding: '10px', borderRadius: '8px' }}>
              <strong>{conta.tipo}</strong> - Ag: {conta.agencia} | C/C: {conta.numero}
              <p>Titular: {conta.nomeCliente}</p>
              <p>Saldo: <span style={{ color: conta.saldo < 0 ? 'red' : 'green' }}>
                R$ {conta.saldo.toFixed(2)}
              </span></p>
            </div>
          ))}
          <input type='text' value={contaOrigem} onChange={(evento) => setContaOrigem(evento.target.value)} />
          <input type="text" value={contaDestino} onChange={(evento) => setContaDestino(evento.target.value)} />
          <input type='number' value={transferencia} onChange={(evento) => setTransferencia(Number(evento.target.value))} />
          <button onClick={btnTransferirClick}>Transferir</button>
        </div>
      </div>
    </>
  )
}

export default App
