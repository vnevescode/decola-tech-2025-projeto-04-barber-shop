# Gerar components para clients
ng g c clients/new-client
ng g c clients/list-clients
ng g c clients/edit-client
ng g c clients/components/client-form
ng g c clients/components/client-table

# Criar arquivos .ts
New-Item -ItemType File src/app/clients/client.models.ts

# Gerar components para schedules
ng g c schedules/schedules-month
ng g c schedules/components/schedule-calendar

# Criar arquivos .ts
New-Item -ItemType File src/app/schedules/schedule.models.ts

# Commons components
ng g c commons/components/card-header
ng g c commons/components/menu-bar
ng g c commons/components/yes-no-dialog

# Services
ng g s services/dialog-manager
ng g s services/snackbar-manager
ng g s services/api-client/clients/clients
ng g s services/api-client/schedules/schedules

# Arquivos de interface e tokens
New-Item -ItemType File src/app/services/idialog-manager.service.ts
New-Item -ItemType File src/app/services/isnackbar-manager.service.ts
New-Item -ItemType File src/app/services/service.token.ts

New-Item -ItemType File src/app/services/api-client/clients/iclients.service.ts
New-Item -ItemType File src/app/services/api-client/clients/client.models.ts

New-Item -ItemType File src/app/services/api-client/schedules/schedules.service.ts
New-Item -ItemType File src/app/services/api-client/schedules/schedule.models.ts

# Instalar pacotes
yarn add @angular/cdk bootstrap ngx-mask