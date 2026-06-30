import { useState } from "react";
import { adminRequest } from "../../api/adminApi";

interface TwoFactorSetupResponse {
  secret: string;
  qrCodeDataUrl: string;
}

export default function AdminSecurity() {
  const [setup, setSetup] = useState<TwoFactorSetupResponse | null>(null);
  const [code, setCode] = useState("");
  const [message, setMessage] = useState("");

  async function startSetup() {
    try {
      setMessage("");
      const data = await adminRequest<TwoFactorSetupResponse>("/admin/security/2fa/setup", {
        method: "POST",
      });

      setSetup(data);
    } catch (error) {
      console.error(error);
      setMessage("Não foi possível iniciar a configuração 2FA.");
    }
  }

  async function confirmSetup(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    try {
      await adminRequest("/admin/security/2fa/confirm", {
        method: "POST",
        body: JSON.stringify({ code }),
      });

      setMessage("2FA ativado com sucesso.");
      setSetup(null);
      setCode("");
    } catch (error) {
      console.error(error);
      setMessage("Código inválido.");
    }
  }

  async function disable2FA() {
    const confirmed = window.confirm("Tem certeza que deseja desativar o 2FA?");

    if (!confirmed) return;

    try {
      await adminRequest("/admin/security/2fa", {
        method: "DELETE",
      });

      setMessage("2FA desativado.");
    } catch (error) {
      console.error(error);
      setMessage("Não foi possível desativar o 2FA.");
    }
  }

  return (
    <div>
      <div className="admin-header">
        <div>
          <h1>Segurança</h1>
          <p>Configure autenticação em duas etapas com app authenticator.</p>
        </div>
      </div>

      {message && <div className="admin-error">{message}</div>}

      <section className="admin-form-card security-card">
        <div>
          <h2>Autenticação em duas etapas</h2>
          <p>
            Use Google Authenticator, Microsoft Authenticator, Authy ou outro app compatível.
          </p>

          <button onClick={startSetup}>Ativar 2FA</button>
          <button className="danger" onClick={disable2FA}>Desativar 2FA</button>
        </div>

        {setup && (
          <div className="two-factor-setup">
            <h3>Escaneie o QR Code</h3>

            <img src={setup.qrCodeDataUrl} alt="QR Code 2FA" />

            <p>Ou digite manualmente:</p>
            <code>{setup.secret}</code>

            <form onSubmit={confirmSetup}>
              <label>Código do app</label>
              <input
                value={code}
                onChange={(event) => setCode(event.target.value)}
                maxLength={6}
                inputMode="numeric"
                placeholder="000000"
              />

              <button>Confirmar 2FA</button>
            </form>
          </div>
        )}
      </section>
    </div>
  );
}