import React, {useState} from 'react';
import {MagnifyingGlassIcon, XMarkIcon} from '@heroicons/react/24/outline';

interface SearchBarProps {
    onSearch: (query: string) => void;
    onClear: () => void;
    placeholder?: string;
    isLoading?: boolean;
}

const SearchBar = ({
                       onSearch,
                       onClear,
                       placeholder = "맛집을 검색해보세요",
                       isLoading = false
                   }: SearchBarProps) => {
    const [searchQuery, setSearchQuery] = useState('');
    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            onSearch(searchQuery.trim());
        }
    };
    const handleClear = () => {
        setSearchQuery('');
        onClear();
    };

    return (
        <div className="w-full max-w-md mx-auto mb-4">
            <form onSubmit={handleSubmit} className="relative">
                <div className="relative flex items-center">
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        placeholder={placeholder}
                        className="w-full pl-10 pr-12 py-3 border-2 border-gray-200 rounded-full focus:border-[#ff5e62] focus:outline-none transition-colors duration-200 text-sm"
                        disabled={isLoading}
                    />
                    <MagnifyingGlassIcon className="absolute left-3 w-5 h-5 text-gray-400"/>
                    {searchQuery && (
                        <button
                            type="button"
                            onClick={handleClear}
                            className="absolute right-3 p-1 text-gray-400 hover:text-gray-600 transition-colors duration-200"
                        >
                            <XMarkIcon className="w-4 h-4"/>
                        </button>
                    )}
                </div>
                {isLoading && (
                    <div className="absolute inset-y-0 right-3 flex items-center">
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-[#ff5e62]"></div>
                    </div>
                )}
            </form>
        </div>
    );
};

export default SearchBar;
