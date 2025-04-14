import {
  trigger,
  style,
  animate,
  transition,
  keyframes,
  state,
} from '@angular/animations';

export const shakeAnimation = trigger('shakeAnimation', [
  state('none', style({ transform: 'translateX(0)' })),
  state('shake', style({ transform: 'translateX(0)' })),
  transition('none => shake', [
    animate(
      '400ms ease-in-out',
      keyframes([
        style({ transform: 'translateX(0)' }),
        style({ transform: 'translateX(-10px)' }),
        style({ transform: 'translateX(10px)' }),
        style({ transform: 'translateX(-10px)' }),
        style({ transform: 'translateX(10px)' }),
        style({ transform: 'translateX(0)' }),
      ])
    ),
  ]),
]);
