#!/usr/bin/env bash

_sem_ver="1.2.1"
_commits="$(git rev-list --count HEAD)"
_hash="$(git rev-parse --short HEAD)"

if [ "${CIRCLE_PROJECT_USERNAME:-}" = "packetloop" ] && [ "${CIRCLE_BRANCH:-}" = "develop" ]; then
  echo "$_sem_ver-$_commits"
else
  echo "$_sem_ver-$_commits-$_hash"
fi
