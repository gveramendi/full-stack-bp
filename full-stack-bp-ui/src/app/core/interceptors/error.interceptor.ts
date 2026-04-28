import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { ApiError } from '../models';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notifier = inject(NotificationService);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      const apiError = err.error as ApiError | null;
      const message = apiError?.message ?? err.message ?? 'Unexpected error';

      if (apiError?.issues?.length) {
        const detail = apiError.issues.map((i) => `${i.field}: ${i.message}`).join(', ');
        notifier.error(`${message} — ${detail}`);
      } else {
        notifier.error(message);
      }

      return throwError(() => err);
    })
  );
};
