#!/usr/bin/env bash
set -e

echo "🔍 Buscando issues abertas atribuídas a você (@me)..."

# Lista apenas as issues com status 'open' atribuídas ao seu usuário
issues_para_fechar=$(gh issue list --assignee "@me" --state open --limit 500 --json number --jq '.[].number')

if [ -z "$issues_para_fechar" ]; then
  echo "✨ Nenhuma issue aberta atribuída a você encontrada!"
  exit 0
fi

total=$(echo "$issues_para_fechar" | wc -w | tr -d ' ')
echo "📋 Total encontrado: $total issues. Iniciando fechamento..."

count=1
for num in $issues_para_fechar; do
  echo "[$count/$total] Fechando issue #$num..."
  gh issue close "$num" --comment "Resolvido via merge na branch develop." > /dev/null
  count=$((count + 1))
  sleep 0.15
done

echo ""
echo "🎉 Concluído! Todas as $total issues atribuídas a você foram fechadas com sucesso."
