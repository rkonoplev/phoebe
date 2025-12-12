import { useState, useRef, useEffect } from 'react';

/**
 * Universal hook for implementing undo functionality on save operations
 * @param {Function} saveFunction - The async function to execute after delay
 * @param {number} delay - Delay in milliseconds before executing save (default: 5000)
 * @returns {Object} - Hook state and methods
 */
const useUndoSave = (saveFunction, delay = 5000) => {
  const [showUndo, setShowUndo] = useState(false);
  const [pendingData, setPendingData] = useState(null);
  const [error, setError] = useState('');
  const saveTimerRef = useRef(null);

  const executeSave = async (data) => {
    setError('');
    setPendingData({ ...data });
    setShowUndo(true);
    
    saveTimerRef.current = setTimeout(async () => {
      try {
        await saveFunction(data);
        setShowUndo(false);
        setPendingData(null);
      } catch (error) {
        console.error('Failed to save:', error);
        setError('Failed to save. Please check your input and try again.');
        setShowUndo(false);
        setPendingData(null);
      }
    }, delay);
  };

  const undoSave = () => {
    if (saveTimerRef.current) {
      clearTimeout(saveTimerRef.current);
      saveTimerRef.current = null;
    }
    setShowUndo(false);
    setPendingData(null);
    setError('');
  };

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      if (saveTimerRef.current) {
        clearTimeout(saveTimerRef.current);
      }
    };
  }, []);

  return {
    showUndo,
    pendingData,
    error,
    executeSave,
    undoSave,
    isProcessing: showUndo
  };
};

export default useUndoSave;