import { Component, OnDestroy, OnInit } from '@angular/core';
import {
    ActivatedRoute,
    NavigationEnd,
    Router,
    RouterOutlet,
} from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { filter, map, Subscription } from 'rxjs';
import { CardHeaderComponent } from './commons/components/card-header/card-header.component';
import { MenuBarComponent } from './commons/components/menu-bar/menu-bar.component';
import { LoadingService } from './services/loading.service';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-root',
    imports: [
        RouterOutlet,
        CardHeaderComponent,
        MenuBarComponent,
        MatProgressSpinnerModule,
        AsyncPipe,
        CommonModule,
    ],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit, OnDestroy {
    title = 'barber-shop-ui';
    loading$;

    private routeSubscription?: Subscription;

    constructor(
        private readonly router: Router,
        private readonly activatedRoute: ActivatedRoute,
        public loadingService: LoadingService
    ) {
        this.loading$ = this.loadingService.isLoading;
    }

    ngOnDestroy(): void {
        if (this.routeSubscription) {
            this.routeSubscription.unsubscribe();
        }
    }

    ngOnInit(): void {
        this.routeSubscription = this.router.events
            .pipe(
                filter((event) => event instanceof NavigationEnd),
                map(() => this.getRouteTitle(this.activatedRoute))
            )
            .subscribe((title) => (this.title = title));
    }

    private getRouteTitle(route: ActivatedRoute): string {
        let child = route;
        while (child.firstChild) {
            child = child.firstChild;
        }
        return child.snapshot.data['title'] || 'Default Title';
    }
}
