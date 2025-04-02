import {
    AfterViewInit,
    Component,
    EventEmitter,
    Inject,
    Input,
    OnChanges,
    OnDestroy,
    SimpleChanges,
    ViewChild,
    Output,
} from '@angular/core';
import { NgForm, FormsModule } from '@angular/forms';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { Subscription } from 'rxjs';

import { SERVICES_TOKEN } from '../../../services/service.token';
import { IDialogManagerService } from '../../../services/idialog-manager.service';
import { DialogManagerService } from '../../../services/dialog-manager.service';
import { YesNoDialogComponent } from '../../../commons/components/yes-no-dialog/yes-no-dialog.component';

import {
    ClientScheduleAppointmentModel,
    SaveScheduleModel,
    ScheduleAppointementMonthModel,
    SelectClientModel,
} from '../../schedule.models';

import { CommonModule } from '@angular/common';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTimepickerModule } from '@angular/material/timepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatRadioModule } from '@angular/material/radio';
import { provideNativeDateAdapter } from '@angular/material/core';

@Component({
    selector: 'app-schedule-calendar',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatDatepickerModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatTooltipModule,
        MatTimepickerModule,
        MatInputModule,
        MatFormFieldModule,
        MatSelectModule,
        MatRadioModule,
        MatTableModule,
        MatPaginatorModule,
    ],
    templateUrl: './schedule-calendar.component.html',
    styleUrls: ['./schedule-calendar.component.scss'],
    providers: [
        provideNativeDateAdapter(),
        {
            provide: SERVICES_TOKEN.DIALOG,
            useClass: DialogManagerService,
        },
    ],
})
export class ScheduleCalendarComponent
    implements OnDestroy, AfterViewInit, OnChanges
{
    @Input() monthSchedule!: ScheduleAppointementMonthModel;
    @Input() clients: SelectClientModel[] = [];

    @Output() onDateChange = new EventEmitter<Date>();
    @Output() onConfirmDelete =
        new EventEmitter<ClientScheduleAppointmentModel>();
    @Output() onScheduleClient = new EventEmitter<SaveScheduleModel>();

    @ViewChild(MatPaginator) paginator!: MatPaginator;

    // Visões disponíveis: 'month' | 'week' | 'day'
    viewMode: 'month' | 'week' | 'day' = 'month';

    // Data atualmente selecionada
    private _selected: Date = new Date();

    // Tabela
    displayedColumns: string[] = ['startAt', 'endAt', 'client', 'actions'];
    dataSource!: MatTableDataSource<ClientScheduleAppointmentModel>;

    // Novo agendamento
    newSchedule: SaveScheduleModel = {
        startAt: undefined,
        endAt: undefined,
        clientId: undefined,
    };

    // Subs para unsubscribe
    private subscription?: Subscription;

    // Filtro de pesquisa (busca por nome do cliente)
    searchTerm: string = '';

    constructor(
        @Inject(SERVICES_TOKEN.DIALOG)
        private readonly dialogManagerService: IDialogManagerService
    ) {}

    /** GET e SET do selected, para disparar onDateChange */
    get selected(): Date {
        return this._selected;
    }
    set selected(selected: Date) {
        // Se a data realmente mudou...
        if (this._selected.getTime() !== selected.getTime()) {
            this._selected = selected;
            this.onDateChange.emit(selected);
            this.buildTable(); // Recarrega a tabela com base na data selecionada
        }
    }

    /** Ciclo de vida */
    ngOnChanges(changes: SimpleChanges): void {
        if (changes['monthSchedule'] && this.monthSchedule) {
            this.buildTable();
        }
    }
    ngAfterViewInit(): void {
        if (this.dataSource && this.paginator) {
            this.dataSource.paginator = this.paginator;
        }
    }
    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    /**
     * Monta a lista de agendamentos (mat-table) filtrada pela data selecionada
     * e também aplica o searchTerm.
     */
    private buildTable(): void {
        if (!this.monthSchedule) return;

        // Filtra os agendamentos do array principal, pegando apenas do dia selecionado (quando viewMode=day)
        // ou da semana selecionada (quando viewMode=week), etc.
        let appointments: ClientScheduleAppointmentModel[] = [];

        // Lembra que monthSchedule.month está 1-based e o JS Date está 0-based para month
        const sameMonth = (a: ClientScheduleAppointmentModel) =>
            this.monthSchedule.year === this._selected.getFullYear() &&
            this.monthSchedule.month - 1 === this._selected.getMonth();

        if (this.viewMode === 'month') {
            // Exemplo: Se for "month", podemos mostrar todos os agendamentos do mês inteiro
            // ou só do dia selecionado (você decide). Aqui vou pegar TODOS do mês, e filtrar por "searchTerm"
            appointments =
                this.monthSchedule.scheduledAppointments.filter(sameMonth);
        } else if (this.viewMode === 'week') {
            // Pega a semana do _selected
            // 1) Descobre o primeiro dia da semana
            const dayOfWeek = this._selected.getDay(); // 0=Domingo, 6=Sábado
            const start = new Date(this._selected);
            start.setDate(this._selected.getDate() - dayOfWeek); // Início da semana (Domingo)
            const end = new Date(start);
            end.setDate(start.getDate() + 6); // Sábado

            appointments = this.monthSchedule.scheduledAppointments.filter(
                (ap) => {
                    const apDate = new Date(
                        this.monthSchedule.year,
                        this.monthSchedule.month - 1,
                        ap.day
                    );
                    return apDate >= start && apDate <= end;
                }
            );
        } else {
            // 'day'
            // Filtra só aquele dia
            appointments = this.monthSchedule.scheduledAppointments.filter(
                (ap) =>
                    this.monthSchedule.year === this._selected.getFullYear() &&
                    this.monthSchedule.month - 1 ===
                        this._selected.getMonth() &&
                    ap.day === this._selected.getDate()
            );
        }

        // Filtro de pesquisa simples (busca no nome do cliente)
        if (this.searchTerm?.trim()) {
            appointments = appointments.filter((a) =>
                a.clientName
                    .toLowerCase()
                    .includes(this.searchTerm.toLowerCase())
            );
        }

        // Cria e atribui no dataSource
        this.dataSource =
            new MatTableDataSource<ClientScheduleAppointmentModel>(
                appointments
            );
        if (this.paginator) {
            this.dataSource.paginator = this.paginator;
        }
    }

    /**
     * Ao enviar o form (criar novo agendamento)
     */
    onSubmit(form: NgForm) {
        if (!this.newSchedule.startAt || !this.newSchedule.clientId) {
            return;
        }

        // Vamos verificar se existe conflito
        if (
            this.hasConflict(this.newSchedule.startAt, this.newSchedule.endAt)
        ) {
            alert('Conflito de horário detectado! Escolha outro intervalo.');
            return;
        }

        // Monte o objeto com dia/horario
        const startAt = new Date(this._selected);
        const endAt = new Date(this._selected);
        startAt.setHours(
            this.newSchedule.startAt.getHours(),
            this.newSchedule.startAt.getMinutes()
        );
        if (this.newSchedule.endAt) {
            endAt.setHours(
                this.newSchedule.endAt.getHours(),
                this.newSchedule.endAt.getMinutes()
            );
        } else {
            // Exemplo: se não tiver endAt, adiciona +1 hora
            endAt.setHours(startAt.getHours() + 1);
        }

        // Monta um obj para exibir na tela
        const saved: ClientScheduleAppointmentModel = {
            id: -1,
            day: this._selected.getDate(),
            startAt,
            endAt,
            clientId: this.newSchedule.clientId,
            clientName:
                this.clients.find((c) => c.id === this.newSchedule.clientId)
                    ?.name || 'Cliente',
        };

        // Salva no monthSchedule do front (até que o back retorne)
        this.monthSchedule.scheduledAppointments.push(saved);

        // Emite para o parent chamar a API
        // Ex: parent chama SchedulesService.save() e recebe o ID, etc.
        this.onScheduleClient.emit(saved);

        this.buildTable();
        form.resetForm();
        this.newSchedule = {
            startAt: undefined,
            endAt: undefined,
            clientId: undefined,
        };
    }

    /**
     * Checa conflito localmente se há algum agendamento que se sobrepõe ao novo
     */
    hasConflict(start: Date, end?: Date): boolean {
        const sEnd = end || new Date(start.getTime() + 60 * 60 * 1000);

        const dayAppointments = this.monthSchedule.scheduledAppointments.filter(
            (a) =>
                a.day === this._selected.getDate() &&
                this.monthSchedule.month - 1 === this._selected.getMonth() &&
                this.monthSchedule.year === this._selected.getFullYear()
        );

        for (const app of dayAppointments) {
            // Converte a string do JSON em Date
            const appStart =
                typeof app.startAt === 'string'
                    ? new Date(app.startAt)
                    : app.startAt;
            const appEnd =
                typeof app.endAt === 'string' ? new Date(app.endAt) : app.endAt;

            // Se (novoStart < existenteEnd) && (novoEnd > existenteStart) => conflito
            if (start < appEnd && sEnd > appStart) {
                return true;
            }
        }
        return false;
    }

    /**
     * Exclusão de agendamento
     */
    requestDelete(schedule: ClientScheduleAppointmentModel) {
        this.subscription = this.dialogManagerService
            .showYesNoDialog(YesNoDialogComponent, {
                title: 'Exclusão de agendamento',
                content: 'Confirma a exclusão do agendamento?',
            })
            .subscribe((result) => {
                if (result) {
                    this.onConfirmDelete.emit(schedule);
                    // Remover do dataSource local
                    const updatedList = this.dataSource.data.filter(
                        (c) => c.id !== schedule.id
                    );
                    this.dataSource =
                        new MatTableDataSource<ClientScheduleAppointmentModel>(
                            updatedList
                        );

                    // Opcional: remover também do array principal monthSchedule
                    const idx =
                        this.monthSchedule.scheduledAppointments.findIndex(
                            (x) => x.id === schedule.id
                        );
                    if (idx >= 0) {
                        this.monthSchedule.scheduledAppointments.splice(idx, 1);
                    }
                    if (this.paginator) {
                        this.dataSource.paginator = this.paginator;
                    }
                }
            });
    }

    /**
     * Se o usuário escolher a hora de início, definimos a hora de término com +1h
     */
    onTimeChange(time: Date) {
        const endAt = new Date(time);
        endAt.setHours(time.getHours() + 1);
        this.newSchedule.endAt = endAt;
    }

    /**
     * dateClass: função do MatCalendar para alterar a classe CSS de cada dia.
     * Aqui, se tiver ao menos 1 agendamento, deixamos o dia "marcado" em outra cor.
     */
    dateClass = (d: Date) => {
        const dayHasAppointment =
            this.monthSchedule?.scheduledAppointments?.some(
                (app) =>
                    app.day === d.getDate() &&
                    this.monthSchedule.month - 1 === d.getMonth() &&
                    this.monthSchedule.year === d.getFullYear()
            );
        return dayHasAppointment ? 'custom-date-has-appointment' : '';
    };

    /**
     * A cada alteração do filtro de busca, reconstruímos a tabela
     */
    onFilterSearch() {
        this.buildTable();
    }

    getTotalAppointments(): number {
        return this.dataSource?.data.length || 0;
    }

    getTotalHours(): number {
        return this.dataSource?.data.reduce((acc, app) => {
            const start = new Date(app.startAt);
            const end = new Date(app.endAt);
            const diffHours =
                (end.getTime() - start.getTime()) / (1000 * 60 * 60);
            return acc + diffHours;
        }, 0);
    }
}
