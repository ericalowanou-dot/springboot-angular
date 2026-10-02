import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ConfirmService } from '../core/confirm.service';
import { ToastService } from '../core/toast.service';
import { IconComponent } from './icon.component';
import { ModalComponent } from './modal.component';

/** Notifications et boîte de confirmation globales, montées une seule fois à la racine. */
@Component({
  selector: 'app-overlays',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent, ModalComponent],
  template: `
    <div class="toasts" aria-live="polite">
      @for (t of toasts.toasts(); track t.id) {
        <div class="toast" [class]="t.type">
          <span class="ico">
            <app-icon [name]="t.type === 'succes' ? 'check' : t.type === 'erreur' ? 'alert' : 'info'" [size]="16" />
          </span>
          <span class="msg">{{ t.message }}</span>
          <button class="icon-btn" (click)="toasts.fermer(t.id)" aria-label="Fermer">
            <app-icon name="x" [size]="14" />
          </button>
        </div>
      }
    </div>

    @if (confirm.demande(); as d) {
      <app-modal [titre]="d.titre" taille="sm" (fermer)="confirm.repondre(false)">
        <p class="texte">{{ d.message }}</p>
        <div pied>
          <button class="btn btn-secondary" (click)="confirm.repondre(false)">Annuler</button>
          <button
            class="btn"
            [class.btn-danger]="d.danger"
            [class.btn-primary]="!d.danger"
            (click)="confirm.repondre(true)"
          >
            {{ d.libelle ?? 'Confirmer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
  styles: `
    .toasts {
      position: fixed;
      right: 20px;
      bottom: 20px;
      z-index: 200;
      display: flex;
      flex-direction: column;
      gap: 10px;
      width: min(380px, calc(100vw - 32px));
    }
    .toast {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 10px 10px 12px;
      border-radius: 14px;
      background: var(--surface);
      border: 1px solid var(--border);
      box-shadow: var(--shadow-lg);
      animation: glisse 0.25s ease-out;
    }
    .ico {
      display: grid;
      place-items: center;
      width: 28px;
      height: 28px;
      border-radius: 8px;
      flex-shrink: 0;
    }
    .succes .ico {
      background: var(--success-soft);
      color: var(--success);
    }
    .erreur .ico {
      background: var(--danger-soft);
      color: var(--danger);
    }
    .info .ico {
      background: var(--info-soft);
      color: var(--info);
    }
    .msg {
      flex: 1;
      font-weight: 500;
    }
    .texte {
      margin: 0;
      color: var(--text-2);
    }
    @keyframes glisse {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
    }
  `,
})
export class OverlaysComponent {
  protected toasts = inject(ToastService);
  protected confirm = inject(ConfirmService);
}
