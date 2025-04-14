import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {
  trigger,
  transition,
  style,
  animate,
  query,
} from '@angular/animations';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  animations: [
    trigger('routeAnimations', [
      transition('* <=> *', [
        query(
          ':enter, :leave',
          style({ position: 'absolute', width: '100%' }),
          { optional: true }
        ),
        query(':enter', [style({ opacity: 0 })], { optional: true }),
        query(
          ':leave',
          [
            style({ opacity: 1 }),
            animate('200ms ease-out', style({ opacity: 0 })),
          ],
          { optional: true }
        ),
        query(
          ':enter',
          [
            style({ opacity: 0 }),
            animate('200ms ease-in', style({ opacity: 1 })),
          ],
          { optional: true }
        ),
      ]),
    ]),
  ],
})
export class AppComponent {
  title = 'barber-shop-ui';

  getAnimation(outlet: RouterOutlet) {
    return outlet?.activatedRouteData?.['animation'];
  }
}
