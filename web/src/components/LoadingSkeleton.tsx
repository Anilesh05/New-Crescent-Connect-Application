import React from 'react';
import { cn } from '../lib/utils';

export const LoadingSkeleton: React.FC<{ className?: string }> = ({ className }) => {
  return (
    <div
      className={cn(
        'animate-pulse rounded-lg bg-slate-200 dark:bg-slate-800',
        className
      )}
    />
  );
};

export const DashboardSkeleton: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => (
          <div key={i} className="h-28 rounded-xl border border-slate-200/80 bg-white p-5 dark:border-crescent-dark-border dark:bg-crescent-dark-card">
            <LoadingSkeleton className="h-4 w-24 mb-3" />
            <LoadingSkeleton className="h-8 w-32" />
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 h-96 rounded-xl border border-slate-200/80 bg-white p-6 dark:border-crescent-dark-border dark:bg-crescent-dark-card">
          <LoadingSkeleton className="h-6 w-48 mb-6" />
          <LoadingSkeleton className="h-72 w-full" />
        </div>
        <div className="h-96 rounded-xl border border-slate-200/80 bg-white p-6 dark:border-crescent-dark-border dark:bg-crescent-dark-card">
          <LoadingSkeleton className="h-6 w-36 mb-6" />
          <div className="space-y-4">
            {[...Array(4)].map((_, i) => (
              <LoadingSkeleton key={i} className="h-14 w-full" />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
