import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ThemeService } from './core/theme.service';
import { OverlaysComponent } from './ui/overlays.component';

@Component({
  selector: 'app-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, OverlaysComponent],
  template: `<router-outlet /><app-overlays />`,
})
export class AppComponent {
  // instancié ici pour appliquer le thème dès le démarrage
  private theme = inject(ThemeService);
}
