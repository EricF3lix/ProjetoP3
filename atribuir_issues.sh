#!/usr/bin/env bash
set -e

echo "🔍 Buscando issues abertas no repositório..."

# Coleta os números de todas as issues do repositório
issues_nums=$(gh issue list --limit 300 --json number --jq '.[].number')

if [ -z "$issues_nums" ]; then
  echo "❌ Nenhuma issue encontrada."
  exit 1
fi

total=$(echo "$issues_nums" | wc -w | tr -d ' ')
echo "📋 Total encontrado: $total issues. Atribuindo a @me..."

count=1
for num in $issues_nums; do
  echo "[$count/$total] Atribuindo issue #$num a você..."
  gh issue edit "$num" --add-assignee "@me" > /dev/null
  count=$((count + 1))
  sleep 0.2
done

echo ""
echo "🎉 Concluído! Todas as $total issues foram atribuídas à sua conta."
