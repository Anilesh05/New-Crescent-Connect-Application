import React from 'react';
import { LucideIcon } from 'lucide-react';
import { cn } from '../lib/utils';

export interface EmptyStateProps {
  icon: LucideIcon;
  title: string;
  description: string;
  action?: {
    label: string;
    onClick: () => void;
  };
  className?: string;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  icon: Icon,
  title,
  description,
  action,
  className,
}) => {
  return (
    <div
      className={cn(
        'flex flex-col items-center justify-center rounded-xl border border-dashed border-slate-300 p-10 text-center dark:border-crescent-dark-border dark:bg-crescent-dark-card/40',
        className
      )}
    >
      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-slate-100 text-slate-500 dark:bg-crescent-navy-900/60 dark:text-slate-400">
        <Icon className="h-6 w-6" />
      </div>
      <h3 className="mt-3 text-base font-semibold text-slate-800 dark:text-slate-200">
        {title}
      </h3>
      <p className="mt-1 max-w-sm text-sm text-slate-500 dark:text-slate-400">
        {description}
      </p>
      {action && (
        <button
          onClick={action.onClick}
          className="mt-4 inline-flex items-center gap-2 rounded-lg bg-crescent-navy-900 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-crescent-navy-800 transition-colors dark:bg-blue-600 dark:hover:bg-blue-700"
        >
          {action.label}
        </button>
      )}
    </div>
  );
};
