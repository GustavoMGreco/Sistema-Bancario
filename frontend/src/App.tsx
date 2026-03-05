import { useEffect, useState } from 'react';
import { api } from './services/api';
import { Conta, Transacao } from './types/banco';

function App() {
  const [contas, setContas] = useState<Conta[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [contaOrigem, setContaOrigem] = useState<string>('');
  const [contaDestino, setContaDestino] = useState<string>('');
  const [transferencia, setTransferencia] = useState<number>(0);
  const [extrato, setExtrato] = useState<Transacao[]>([]);
  const [filtroExtrato, setFiltroExtrato] = useState<string | null>(null);

  const [telaAtiva, setTelaAtiva] = useState<string>('dashboard');

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

  async function btnExtratoClick(numero: string) {
    try {

      setFiltroExtrato(numero);
      setTelaAtiva('extrato');

    } catch (err: unknown) {
      setErro(err instanceof Error ? err.message : 'Erro desconhecido')
    }
  }

  useEffect(() => {
    api.listarContas()
      .then(setContas)
      .catch((err: Error) => setErro(err.message));
  }, []);

 useEffect(() => {
  if (telaAtiva === 'extrato' && filtroExtrato) {
    api.buscarExtrato(filtroExtrato)
      .then(setExtrato)
      .catch((err: Error) => setErro(err.message));
  } else if (!filtroExtrato) {
    setExtrato([]); 
  }
}, [filtroExtrato, telaAtiva]);

const extratoFiltrado = extrato;

return (
    <>
      <div className="hexa-wrap">

        {/* MENU LATERAL */}
        <aside className="hexa-sidebar">
          <h2 className="hexa-logo">Banco Hexa</h2>
          <button
            onClick={() => setTelaAtiva('dashboard')}
            className={`hexa-nav-btn ${telaAtiva === 'dashboard' ? 'active' : ''}`}>
            Contas
          </button>
          <button
            onClick={() => setTelaAtiva('transferencia')}
            className={`hexa-nav-btn ${telaAtiva === 'transferencia' ? 'active' : ''}`}>
            Transferência
          </button>
          <button
            onClick={() => { setFiltroExtrato(null); setTelaAtiva('extrato'); }}
            className={`hexa-nav-btn ${telaAtiva === 'extrato' ? 'active' : ''}`}>
            Extrato
          </button>
        </aside>

        {/* ÁREA DE CONTEÚDO */}
        <main className="hexa-main">

          {erro && <div className="hexa-erro">{erro}</div>}

          {/* TELA DE CONTAS */}
          {telaAtiva === 'dashboard' && (
            <div>
              <h1 className="hexa-page-title">Visão Geral</h1>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '16px' }}>
                {contas.map(conta => (
                  <div key={conta.numero} className="hexa-account-card" style={{ marginBottom: 0 }}>
                    <div className="hexa-card-type">{conta.tipo}</div>
                    <div className="hexa-card-meta">Ag: {conta.agencia} &nbsp;|&nbsp; C/C: {conta.numero}</div>
                    <div className="hexa-card-name">Titular: {conta.nomeCliente}</div>
                    <div className="hexa-card-saldo-label">Saldo disponível</div>
                    <div className="hexa-card-saldo" style={{ color: conta.saldo < 0 ? '#f87171' : '#34d399' }}>
                      R$ {conta.saldo.toFixed(2)}
                    </div>
                    <button onClick={() => btnExtratoClick(conta.numero)} className="hexa-extrato-btn">
                      Ver Extrato →
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* TELA DE TRANSFERÊNCIA */}
          {telaAtiva === 'transferencia' && (
            <div className="hexa-transferencia-wrap">
              <h1 className="hexa-page-title">Nova Transferência</h1>
              <div className="hexa-form-card">
                <input type='text' value={contaOrigem} onChange={(evento) => setContaOrigem(evento.target.value)} placeholder="Conta de origem" className="hexa-input" />
                <input type="text" value={contaDestino} onChange={(evento) => setContaDestino(evento.target.value)} placeholder="Conta de destino" className="hexa-input" />
                <input type='number' value={transferencia} onChange={(evento) => setTransferencia(Number(evento.target.value))} placeholder="Valor (R$)" className="hexa-input" />
                <button onClick={btnTransferirClick} className="hexa-submit-btn">Transferir</button>
              </div>
            </div>
          )}

          {/* TELA DE EXTRATO */}
          {telaAtiva === 'extrato' && (
            <div>
              <h1 className="hexa-page-title">Histórico de Transações</h1>

              {/* FILTROS */}
              <div className="hexa-filtros">
                {contas.map(conta => (
                  <button
                    key={conta.numero}
                    className={`hexa-filtro-btn ${filtroExtrato === conta.numero ? 'active' : ''}`}
                    onClick={() => setFiltroExtrato(conta.numero)}>
                    {conta.nomeCliente} · {conta.numero}
                  </button>
                ))}
              </div>

              {extratoFiltrado.length === 0 ? (
                <div className="hexa-tx-empty">Nenhuma transação encontrada.</div>
              ) : (
                extratoFiltrado.map((transacao, index) => (
                  <div key={index} className="hexa-tx-row">
                    <div>
                      <div className="hexa-tx-accounts">
                        <span>{transacao.contaOrigem}</span>
                        <span className="hexa-tx-arrow">→</span>
                        <span>{transacao.contaDestino}</span>
                      </div>
                      <div className="hexa-tx-date">{transacao.dataHora}</div>
                    </div>
                    <div className="hexa-tx-value">R$ {Number(transacao.valor).toFixed(2)}</div>
                  </div>
                ))
              )}
            </div>
          )}

        </main>
      </div>
    </>
  )
}

export default App