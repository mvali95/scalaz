package scalaz

private[scalaz] trait CompositionFunctor[F[_], G[_]] extends Functor[({type λ[α] = F[G[α]]})#λ] {
  implicit def F: Functor[F]

  implicit def G: Functor[G]

  override def map[A, B](fga: F[G[A]])(f: (A) => B): F[G[B]] = F.map(fga)(ga => G.map(ga)(f))
}

private[scalaz] trait CompositionPointed[F[_], G[_]] extends Pointed[({type λ[α] = F[G[α]]})#λ] with CompositionFunctor[F, G] {
  implicit def F: Pointed[F]

  implicit def G: Pointed[G]

  def point[A](a: => A): F[G[A]] = F.point(G.point(a))
}

private[scalaz] trait CompositionApplicative[F[_], G[_]] extends Applicative[({type λ[α] = F[G[α]]})#λ] with CompositionPointed[F, G] with CompositionFunctor[F, G] {
  implicit def F: Applicative[F]

  implicit def G: Applicative[G]

  def ap[A, B](fa: => F[G[A]])(f: => F[G[A => B]]): F[G[B]] =
    F.map2(f, fa)((ff, ga) => G.ap(ga)(ff))
}

private[scalaz] trait CompositionApplicativePlus[F[_], G[_]] extends ApplicativePlus[({type λ[α] = F[G[α]]})#λ] with CompositionPointed[F, G] with CompositionFunctor[F, G] with CompositionApplicative[F, G] {
  implicit def F: ApplicativePlus[F]

  implicit def G: ApplicativePlus[G]

  def empty[A]: F[G[A]] = F.empty[G[A]]
  def plus[A](a: F[G[A]], b: => F[G[A]]): F[G[A]] =
    F.map2(a, b)(G.plus(_, _))
}

private[scalaz] trait CompositionFoldable[F[_], G[_]] extends Foldable[({type λ[α] = F[G[α]]})#λ]  {
  implicit def F: Foldable[F]

  implicit def G: Foldable[G]

  def foldRight[A, B](fa: F[G[A]], z: => B)(f: (A, => B) => B): B =
    F.foldRight(fa, z)((a, b) => G.foldRight(a, b)(f))

  def foldMap[A,B](fa: F[G[A]])(f: A => B)(implicit M: Monoid[B]): B =
    F.foldMap(fa)(G.foldMap(_)(f))

  override def foldLeft[A, B](fa: F[G[A]], z: B)(f: (B, A) => B): B =
    F.foldLeft(fa, z)((b, a) => G.foldLeft(a, b)(f))

}
