import React, { useRef, useEffect } from 'react';
import PlaceCard from './PlaceCard';
import type {Place} from '../../types/place';

interface HorizontalPlaceCardListProps {
  places: Place[];
  onCardHover?: (idx: number) => void;
  onCardLeave?: () => void;
  className?: string;
  containerStyle?: React.CSSProperties;
}

const HorizontalPlaceCardList = ({
  places,
  onCardHover,
  onCardLeave,
  className = "",
  containerStyle = { minHeight: 220, height: 260 }
}: HorizontalPlaceCardListProps) => {
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const container = scrollContainerRef.current;
    if (!container) return;

    const handleWheel = (e: WheelEvent) => {
      if (Math.abs(e.deltaY) > Math.abs(e.deltaX)) {
        e.preventDefault();
        e.stopPropagation();
        container.scrollLeft += e.deltaY * 3;
      }
    };

    container.addEventListener('wheel', handleWheel, { passive: false });

    return () => {
      container.removeEventListener('wheel', handleWheel);
    };
  }, []);

  return (
    <div className={`w-full relative ${className}`} style={containerStyle}>
      <div
        ref={scrollContainerRef}
        id="horizontal-scroll-list"
        className="overflow-x-auto whitespace-nowrap"
        style={{
          scrollBehavior: 'smooth',
          scrollbarWidth: 'none',
          msOverflowStyle: 'none'
        }}
      >
        <div className="flex flex-row items-center h-full gap-4 px-8">
          {places.map((place, idx) =>
            place ? (
              <div
                key={place.id}
                className="inline-block"
                style={{ minWidth: 260, maxWidth: 320 }}
                onMouseEnter={() => onCardHover?.(idx)}
                onMouseLeave={() => onCardLeave?.()}
              >
                <PlaceCard place={place} />
              </div>
            ) : null
          )}
        </div>
      </div>
    </div>
  );
};

export default HorizontalPlaceCardList;
