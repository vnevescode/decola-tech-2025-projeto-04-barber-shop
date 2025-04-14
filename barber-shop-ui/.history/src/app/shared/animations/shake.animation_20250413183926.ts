import {
  animation,
  style,
  animate,
  keyframes,
  trigger,
  transition,
} from '@angular/animations';

export const shake = trigger('shake', [
  transition(':enter', []),
  transition('* => shake', [
    animate(
      '400ms',
      keyframes([
        style({ transform: 'translateX(0)', offset: 0 }),
        style({ transform: 'translateX(-10px)', offset: 0.25 }),
        style({ transform: 'translateX(10px)', offset: 0.5 }),
        style({ transform: 'translateX(-10px)', offset: 0.75 }),
        style({ transform: 'translateX(0)', offset: 1.0 }),
      ])
    ),
  ]),
]);
