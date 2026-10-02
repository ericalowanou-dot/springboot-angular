import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { IconComponent } from './icon.component';

@Component({
  selector: 'app-modal',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent],
  host: { '(document:keydown.escape)': 'fermer.emit()' },
  template: `
    <div class="overlay" (click)="fermer.emit()"></div>
    <section
      class="dialog"
      [class.lg]="taille() === 'lg'"
      [class.sm]="taille() === 'sm'"
      role="dialog"
      aria-modal="true"
      [attr.aria-label]="titre()"
    >
      <header>
        <div>
          <h2>{{ titre() }}</h2>
          @if (sousTitre()) {
            <p>{{ sousTitre() }}</p>
          }
        </div>
        <button type="button" class="icon-btn" (click)="fermer.emit()" aria-label="Fermer">
          <app-icon name="x" />
        </button>
      </header>
      <div class="content"><ng-content /></div>
      <footer><ng-content select="[pied]" /></footer>
    </section>
  `,
  styles: `
    :host {
      position: fixed;
      inset: 0;
      z-index: 100;
      display: grid;
      place-items: center;
      padding: 16px;
    }
    .overlay {
      position: absolute;
      inset: 0;
      background: rgb(12 10 9 / 0.5);
      backdrop-filter: blur(3px);
      animation: fondu 0.18s ease-out;
    }
    .dialog {
      position: relative;
      width: min(560px, 100%);
      max-height: calc(100dvh - 32px);
      display: flex;
      flex-direction: column;
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 20px;
      box-shadow: var(--shadow-lg);
      animation: entree 0.22s cubic-bezier(0.2, 0.9, 0.3, 1.2);
    }
    .dialog.lg {
      width: min(860px, 100%);
    }
    .dialog.sm {
      width: min(420px, 100%);
    }
    header {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 12px;
      padding: 20px 22px 4px;
    }
    h2 {
      font-size: 18px;
      font-weight: 800;
    }
    header p {
      margin: 4px 0 0;
      color: var(--muted);
    }
    .content {
      padding: 16px 22px;
      overflow-y: auto;
    }
    footer {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
      padding: 0 22px 20px;
    }
    footer:empty {
      display: none;
    }
    @keyframes fondu {
      from {
        opacity: 0;
      }
    }
    @keyframes entree {
      from {
        opacity: 0;
        transform: translateY(12px) scale(0.98);
      }
    }
  `,
})
export class ModalComponent {
  readonly titre = input.required<string>();
  readonly sousTitre = input<string>();
  readonly taille = input<'sm' | 'md' | 'lg'>('md');
  readonly fermer = output<void>();
}
