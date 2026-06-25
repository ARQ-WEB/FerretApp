import { Component, signal, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './landing.html',
  styleUrl: './landing.css'
})
export class Landing {
  menuAbierto = signal(false);
  tabActivo = signal('dashboard');
  faqAbierto = signal<number | null>(null);
  scrolled = signal(false);

  formulario = {
    nombre: '',
    email: '',
    telefono: '',
    ferreteria: '',
    mensaje: ''
  };

  faqs = [
    {
      pregunta: '¿Puedo usar FerretApp sin conocimientos técnicos?',
      respuesta: 'Sí, FerretApp fue diseñado para ser intuitivo. Cualquier persona puede aprender a usarlo en menos de una hora con nuestro tutorial guiado.'
    },
    {
      pregunta: '¿Puedo importar mi inventario actual?',
      respuesta: 'Sí, FerretApp soporta importación masiva desde Excel o CSV. Nuestro equipo también puede asistirte en la migración de datos.'
    },
    {
      pregunta: '¿Qué tan seguro es el sistema del producto?',
      respuesta: 'FerretApp usa cifrado de extremo a extremo y backups automáticos diarios. Tus datos están protegidos con los más altos estándares de seguridad.'
    },
    {
      pregunta: '¿Ofrecen soporte en español?',
      respuesta: 'Sí, nuestro equipo de soporte está disponible 24/7 en español vía chat, teléfono y correo electrónico.'
    }
  ];

  tabs = [
    { id: 'dashboard', label: 'Dashboard' },
    { id: 'inventario', label: 'Inventario' },
    { id: 'ventas', label: 'Ventas' },
    { id: 'reportes', label: 'Reportes' }
  ];

  constructor(private router: Router) {}

  @HostListener('window:scroll')
  onScroll() {
    this.scrolled.set(window.scrollY > 40);
  }

  toggleMenu() {
    this.menuAbierto.update(v => !v);
  }

  toggleFaq(i: number) {
    this.faqAbierto.update(v => v === i ? null : i);
  }

  setTab(id: string) {
    this.tabActivo.set(id);
  }

  irAlLogin() {
    this.router.navigate(['/login']);
  }

  scrollTo(id: string) {
    this.menuAbierto.set(false);
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });
  }

  enviarFormulario() {
    alert('¡Gracias! Nos pondremos en contacto contigo pronto.');
    this.formulario = { nombre: '', email: '', telefono: '', ferreteria: '', mensaje: '' };
  }
}
