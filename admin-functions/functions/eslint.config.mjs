import eslint from '@eslint/js';
import tseslint from 'typescript-eslint';

export default tseslint.config(
  eslint.configs.recommended,
  ...tseslint.configs.recommended,
  {
    rules: {
      // allow `any` in legacy code
      '@typescript-eslint/no-explicit-any': 'off',
      // code uses CJS require()
      '@typescript-eslint/no-require-imports': 'off',
      // empty catch blocks allowed
      'no-empty': ['error', { allowEmptyCatch: true }],
    },
  },
  {
    ignores: ['lib/', 'node_modules/'],
  },
);
