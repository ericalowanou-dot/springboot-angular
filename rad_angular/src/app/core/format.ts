import { Pipe, PipeTransform } from '@angular/core';
import { environment } from '../../environments/environment';

export const STATUTS: Partial<Record<string, { label: string; ton: string }>> = {
  EN_ATTENTE: { label: 'En attente', ton: 'warning' },
  EN_PREPARATION: { label: 'En préparation', ton: 'info' },
  PRETE: { label: 'Prête', ton: 'violet' },
  SERVIE: { label: 'Servie', ton: 'success' },
  LIVREE: { label: 'Livrée', ton: 'success' },
  ANNULEE: { label: 'Annulée', ton: 'danger' },
  // livraisons
  A_ASSIGNER: { label: 'À assigner', ton: 'warning' },
  ASSIGNEE: { label: 'Assignée', ton: 'violet' },
  EN_COURS: { label: 'En cours', ton: 'info' },
  ECHOUEE: { label: 'Échouée', ton: 'danger' },
  // approvisionnements
  RECUE: { label: 'Reçue', ton: 'success' },
};

export const LIBELLES: Record<string, Record<string, string>> = {
  type: { SUR_PLACE: 'Sur place', A_EMPORTER: 'À emporter', LIVRAISON: 'Livraison' },
  methode: { ESPECES: 'Espèces', CARTE: 'Carte bancaire', MOBILE_MONEY: 'Mobile Money' },
  fonction: {
    CHEF: 'Chef cuisinier',
    CUISINIER: 'Cuisinier',
    SERVEUR: 'Serveur',
    LIVREUR: 'Livreur',
    CAISSIER: 'Caissier',
    GERANT: 'Gérant',
  },
  role: { ADMIN: 'Administrateur', GERANT: 'Gérant', EMPLOYE: 'Employé', LIVREUR: 'Livreur', CLIENT: 'Client' },
};

const nombre = new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 });

export function fcfa(valeur: number | null | undefined): string {
  return `${nombre.format(Math.round(valeur ?? 0))} FCFA`;
}

/** Les images servies par l'API sont relatives (/api/images/...) : on les préfixe. */
export function urlImage(url: string | null | undefined): string | null {
  if (!url) return null;
  return url.startsWith('/api/') ? environment.apiUrl + url : url;
}

/** Visuel de repli quand un plat n'a pas de photo. */
export function emojiCategorie(nom?: string | null): string {
  const n = (nom ?? '').toLowerCase();
  if (n.includes('entr')) return '🥗';
  if (n.includes('grill')) return '🔥';
  if (n.includes('dessert')) return '🍨';
  if (n.includes('boisson')) return '🥤';
  if (n.includes('plat')) return '🍛';
  return '🍽️';
}

/** Nom à afficher pour une commande : fiche client, sinon coordonnées saisies en ligne. */
export function nomClient(c: {
  client?: { prenom: string; nom: string } | null;
  nomContact?: string | null;
}): string {
  if (c.client) return `${c.client.prenom} ${c.client.nom}`;
  return c.nomContact || 'Client de passage';
}

export function initiales(prenom?: string | null, nom?: string | null): string {
  return `${(prenom ?? '').charAt(0)}${(nom ?? '').charAt(0)}`.toUpperCase() || '?';
}

@Pipe({ name: 'fcfa' })
export class FcfaPipe implements PipeTransform {
  transform(v: number | null | undefined): string {
    return fcfa(v);
  }
}

@Pipe({ name: 'img' })
export class ImagePipe implements PipeTransform {
  transform(v: string | null | undefined): string | null {
    return urlImage(v);
  }
}

@Pipe({ name: 'libelle' })
export class LibellePipe implements PipeTransform {
  transform(v: string | null | undefined, famille: string): string {
    if (!v) return '—';
    if (famille === 'statut') return STATUTS[v]?.label ?? v;
    return LIBELLES[famille]?.[v] ?? v;
  }
}
